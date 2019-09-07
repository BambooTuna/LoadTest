package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, MediaTypes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.domain.model.dsp.UserInfo
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.ClickThroughRate
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}
import io.circe.syntax._
import io.circe.generic.auto._

case class GetModelUseCase() extends UseCaseCommon with FailFastCirceSupport {

  def run(arg: GetModelCommandRequest): Task[GetModelCommandResponse] = {
    (for {
      ctr <- calculateModel(arg.userInfo)
    } yield ctr)
      .map { result =>
        GetModelCommandSucceeded(result)
      }.onErrorHandle { ex =>
        GetModelCommandFailed(ex.getMessage)
      }
  }

  def calculateModel(userInfo: UserInfo): Task[ClickThroughRate] = {
    Task{
      ClickThroughRate(0.05)
    }
  }

  private case class GetModelRequestJson(user_info: UserInfoJson)
  private case class UserInfoJson(user_id: String,
                                  advertiser_id: Int,
                                  game_install_count: Long)
  private case class GetModelResponseJson(ctr: Double)

  def runWithOutSide(arg: GetModelCommandRequest)
                    (implicit system: ActorSystem, mat: Materializer): Task[GetModelCommandResponse] = {
    val request = HttpRequest(POST, s"/calculate_ctr")
      .withEntity(HttpEntity(MediaTypes.`application/json`, convertToJson(arg).asJson.noSpaces))
    Task
      .deferFutureAction { implicit ec =>
        Http()
          .singleRequest(request)
          .flatMap(handleErrorResponse(_)(_.to[GetModelResponseJson]))
          .recover {
            case e =>
              GetModelCommandFailed(s"GetModelCommandFailed: ${e.getMessage}")
          }
      }
  }

  private def convertToJson(arg: GetModelCommandRequest): GetModelRequestJson =
    GetModelRequestJson(UserInfoJson(
      arg.userInfo.userId.value,
      arg.userInfo.advertiserId.value,
      arg.userInfo.gameInstallCount.value,
    ))

  private def convertToAggregate(arg: GetModelResponseJson): GetModelCommandSucceeded =
    GetModelCommandSucceeded(ClickThroughRate(arg.ctr))

  private def handleErrorResponse(r: HttpResponse)
                                 (f: Unmarshal[HttpResponse] => Future[GetModelResponseJson])
                                 (implicit ec: ExecutionContext, mat: Materializer): Future[GetModelCommandResponse] =
  {
    val unmarshaled: Unmarshal[HttpResponse] = Unmarshal(r)
    if (r.status.isSuccess)
      f(unmarshaled).map(convertToAggregate)
    else
      Future.failed(new Exception(s"status is not Success: $r"))
  }

}
