package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{ HttpEntity, HttpRequest, HttpResponse, MediaTypes }
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.domain.setting.TimeZoneSetting
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol._
import com.github.BambooTuna.LoadTest.usecase.calculate.CalculateModelUseCase
import com.github.BambooTuna.LoadTest.usecase.json.{ GetModelRequestJson, GetModelResponseJson }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import monix.eval.Task

import scala.concurrent.{ ExecutionContext, Future }
import io.circe.syntax._
import io.circe.generic.auto._

case class GetModelUseCaseImpl(calculateModelUseCase: CalculateModelUseCase)
    extends GetModelUseCase
    with FailFastCirceSupport {

  //TODO calculateModelUseCaseを作る（時間あれば）
  override def run(arg: GetModelCommandRequest): Task[GetModelCommandResponse] = {
    setResponseTimer
    (for {
      ctr <- calculateModelUseCase.run(arg.request)
    } yield ctr)
      .map { result =>
        successCounterIncrement
        GetModelCommandSucceeded(result)
      }.onErrorHandle { ex =>
        failedCounterIncrement
        GetModelCommandFailed(ex.getMessage)
      }
  }

  override def runWithOutSide(arg: GetModelCommandRequest)(implicit system: ActorSystem,
                                                           mat: Materializer): Task[GetModelCommandResponse] = {
    val request = HttpRequest(POST, s"/calculate_ctr")
      .withEntity(HttpEntity(MediaTypes.`application/json`, convertToJsonObj(arg).asJson.noSpaces))
    Task
      .deferFutureAction { implicit ec =>
        Http()
          .singleRequest(request)
          .flatMap(handleErrorResponse(_)(_.to[GetModelResponseJson]))
          .recover {
            case _ =>
              setResponseTimer
              failedCounterIncrement
              GetModelCommandFailed("GetModelCommandFailed")
          }
      }.timeout(TimeZoneSetting.timeout)
  }

  private def convertToJsonObj(arg: GetModelCommandRequest): GetModelRequestJson =
    arg.request

  private def handleErrorResponse(
      r: HttpResponse
  )(
      f: Unmarshal[HttpResponse] => Future[GetModelResponseJson]
  )(
      implicit ec: ExecutionContext,
      mat: Materializer
  ): Future[GetModelCommandResponse] = {
    val unmarshaled: Unmarshal[HttpResponse] = Unmarshal(r)
    if (r.status.isSuccess)
      f(unmarshaled).map(GetModelCommandSucceeded)
    else
      Future {
        setResponseTimer
        failedCounterIncrement
        GetModelCommandFailed("GetModelCommandFailed")
      }
  }

}
