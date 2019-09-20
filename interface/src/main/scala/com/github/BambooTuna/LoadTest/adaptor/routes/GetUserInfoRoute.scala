package com.github.BambooTuna.LoadTest.adaptor.routes

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import akka.http.scaladsl.server.Directives.{
  as,
  complete,
  entity,
  extractActorSystem,
  extractRequestContext,
  onSuccess
}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes.json._
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.UserDeviceId
import com.github.BambooTuna.LoadTest.usecase.GetUserInfoUseCase
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import com.github.BambooTuna.LoadTest.usecase.json.UserInfoJson
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import monix.execution.Scheduler.Implicits.global

case class GetUserInfoRoute(useCase: GetUserInfoUseCase)(implicit materializer: ActorMaterializer)
    extends RouteCommonSetting
    with FailFastCirceSupport {

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { _ =>
      entity(as[GetUserInfoRequestJson]) { json =>
        jsonParseHandle {
          val f =
            useCase
              .run(
                GetUserInfoCommandRequest(
                  UserDeviceId(json.device_id)
                )
              )
              .runToFuture
          onSuccess(f) {
            case GetUserInfoCommandSucceeded(response) =>
              val result =
                GetUserInfoResponseJson(
                  Some(
                    UserInfoJson(
                      device_id = response.userId.value,
                      advertiser_id = response.advertiserId.value,
                      game_install_count = response.gameInstallCount.value
                    )
                  )
                )
              val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
              complete(StatusCodes.OK, entity)
            case GetUserInfoCommandFailed(e) =>
              val result = GetUserInfoResponseJson(error_messages = Seq(e))
              val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
              complete(StatusCodes.BadRequest, entity)
            case e =>
              val entity = HttpEntity(MediaTypes.`application/json`, e.toString)
              complete(StatusCodes.BadRequest, entity)
          }
        } { _ =>
          val entity = HttpEntity(MediaTypes.`application/json`, "json parse error!")
          complete(StatusCodes.MisdirectedRequest, entity)
        }
      }
    }
  }

}
