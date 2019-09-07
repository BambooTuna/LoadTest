package com.github.BambooTuna.LoadTest.usecase

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.BudgetRepositoryOnRedis
import com.github.BambooTuna.LoadTest.domain.model.ad.AdvertiserId
import com.github.BambooTuna.LoadTest.domain.setting.TimeZoneSetting
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol._
import com.github.BambooTuna.LoadTest.usecase.json.{ GetBudgetRequestJson, GetBudgetResponseJson }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import monix.eval.Task

import scala.concurrent.{ ExecutionContext, Future }

case class GetBudgetUseCaseImpl(budgetRepositoriesOnRedis: GetBudgetRepositoryBalance[BudgetRepositoryOnRedis])
    extends GetBudgetUseCase
    with FailFastCirceSupport {

  //TODO budgetRepositoryを作る（時間あれば）
  override def run(arg: GetBudgetCommandRequest): Task[GetBudgetCommandResponse] = {
    setResponseTimer
    (for {
      aggregate <- Task.pure(
        AdvertiserId(arg.request.advertiser_id)
      )
      budget <- budgetRepositoriesOnRedis
        .getConnectionWithAdvertiserId(aggregate).resolveById(aggregate).timeout(TimeZoneSetting.timeout)
    } yield budget)
      .map { result =>
        successCounterIncrement
        GetBudgetCommandSucceeded(GetBudgetResponseJson(result.value))
      }.onErrorHandle { ex =>
        failedCounterIncrement
        GetBudgetCommandFailed(ex.getMessage)
      }
  }

//  override def runWithOutSide(arg: GetBudgetCommandRequest)(implicit system: ActorSystem,
//                                                            mat: Materializer): Task[GetBudgetCommandResponse] = {
//    val request = HttpRequest(POST, s"/budget")
//      .withEntity(HttpEntity(MediaTypes.`application/json`, convertToJsonObj(arg).asJson.noSpaces))
//    Task
//      .deferFutureAction { implicit ec =>
//        Http()
//          .singleRequest(request)
//          .flatMap(handleErrorResponse(_)(_.to[GetBudgetResponseJson]))
//          .recover {
//            case _ =>
//              getBudgetCommandFailedTaskCounter.increment()
//              logger.debug("GetBudgetCommandFailed")
//              GetBudgetCommandFailed("GetBudgetCommandFailed")
//          }
//      }.timeout(TimeZoneSetting.timeout)
//  }

  private def convertToJsonObj(arg: GetBudgetCommandRequest): GetBudgetRequestJson =
    arg.request

  private def handleErrorResponse(
      r: HttpResponse
  )(
      f: Unmarshal[HttpResponse] => Future[GetBudgetResponseJson]
  )(
      implicit ec: ExecutionContext,
      mat: Materializer
  ): Future[GetBudgetCommandResponse] = {
    val unmarshaled: Unmarshal[HttpResponse] = Unmarshal(r)
    if (r.status.isSuccess)
      f(unmarshaled).map(GetBudgetCommandSucceeded)
    else
      Future {
        setResponseTimer
        failedCounterIncrement
        GetBudgetCommandFailed("GetBudgetCommandFailed")
      }
  }

}
