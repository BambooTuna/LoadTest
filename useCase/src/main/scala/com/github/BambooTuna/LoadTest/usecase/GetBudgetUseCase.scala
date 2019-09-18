package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{ HttpEntity, HttpRequest, HttpResponse, MediaTypes }
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.BudgetRepositoryOnRedis
import com.github.BambooTuna.LoadTest.domain.model.budget.BudgetBalance
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{ GetBudgetCommandResponse, _ }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import monix.eval.Task
import io.circe.syntax._
import io.circe.generic.auto._

import scala.concurrent.{ ExecutionContext, Future }

case class GetBudgetUseCase(budgetRepositories: BudgetRepositoryBalancer[BudgetRepositoryOnRedis])
    extends UseCaseCommon
    with FailFastCirceSupport {

  def run(arg: GetBudgetCommandRequest): Task[GetBudgetCommandResponse] = {
    (for {
      _ <- Task.pure(
        setResponseTimer
      )
      aggregate <- Task.pure(
        arg.advertiserId
      )
      budget <- budgetRepositories
        .getConnectionWithAdvertiserId(aggregate).resolveById(aggregate)
    } yield budget)
      .map { result =>
        GetBudgetCommandSucceeded(
          BudgetBalance(1) //TODO
        )
      }
      .onErrorHandle { ex =>
        failedCounterIncrement
        GetBudgetCommandFailed(ex.getMessage)
      }
      .doOnFinish(_ => Task.pure(recodeResponseTime))
  }

  private case class GetBudgetRequestJson(advertiser_id: Int)
  private case class GetBudgetResponseJson(budget_balance: Double)

  def runWithOtherServer(arg: GetBudgetCommandRequest)(implicit system: ActorSystem,
                                                       mat: Materializer): Task[GetBudgetCommandResponse] = {
    val request = HttpRequest(GET, s"/budget")
      .withEntity(HttpEntity(MediaTypes.`application/json`, convertToJson(arg).asJson.noSpaces))
    Task
      .deferFutureAction { implicit ec =>
        Http()
          .singleRequest(request)
          .flatMap(handleErrorResponse(_)(_.to[GetBudgetResponseJson]))
          .recover {
            case e =>
              GetBudgetCommandFailed(s"GetBudgetCommandFailed: ${e.getMessage}")
          }
      }
  }

  private def convertToJson(arg: GetBudgetCommandRequest): GetBudgetRequestJson =
    GetBudgetRequestJson(arg.advertiserId.value)

  private def convertToAggregate(arg: GetBudgetResponseJson): GetBudgetCommandSucceeded =
    GetBudgetCommandSucceeded(BudgetBalance(arg.budget_balance))

  private def handleErrorResponse(r: HttpResponse)(
      f: Unmarshal[HttpResponse] => Future[GetBudgetResponseJson]
  )(implicit ec: ExecutionContext, mat: Materializer): Future[GetBudgetCommandResponse] = {
    val unmarshaled: Unmarshal[HttpResponse] = Unmarshal(r)
    if (r.status.isSuccess)
      f(unmarshaled).map(convertToAggregate)
    else
      Future.failed(new Exception(s"status is not Success: $r"))
  }

}
