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
import com.github.BambooTuna.LoadTest.adaptor.routes.json.{
  AddUserInfoRequestJson,
  AddUserInfoResponseJson,
  DeviceIdJson
}
import com.github.BambooTuna.LoadTest.domain.model.dsp.UserInfo
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.{ AdvertiserId, UserDeviceId }
import com.github.BambooTuna.LoadTest.domain.model.dsp.user.GameInstallCount
import com.github.BambooTuna.LoadTest.usecase.AddUserInfoUseCase
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import monix.execution.Scheduler.Implicits.global
import io.circe.syntax._
import io.circe.generic.auto._
import monix.eval.Task

case class AddUserInfoRoute(useCase: AddUserInfoUseCase)(implicit materializer: ActorMaterializer)
    extends RouteCommonSetting
    with FailFastCirceSupport {

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { _ =>
      val time = java.time.Instant.now().toEpochMilli
      successCounter.increment()
      entity(as[AddUserInfoRequestJson]) { jsones =>
        val fs =
          jsones.data.map { json =>
            useCase
              .run(
                AddUserInfoCommandRequest(
                  UserInfo(
                    UserDeviceId(json.device_id),
                    AdvertiserId(json.advertiser_id),
                    GameInstallCount(json.game_install_count)
                  )
                )
              )
          }
        val f = Task.sequence(fs).runToFuture
        onSuccess(f) {
          case response: Seq[AddUserInfoCommandSucceeded] =>
            val result =
              AddUserInfoResponseJson(
                response
                  .map(r => DeviceIdJson(r.deviceId.value))
              )
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            complete(StatusCodes.OK, entity)
          case failedResponse: Seq[AddUserInfoCommandFailed] =>
            val result =
              AddUserInfoResponseJson(
                Seq.empty,
                failedResponse.map(_.error)
              )
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            complete(StatusCodes.BadRequest, entity)
          case e =>
            val entity = HttpEntity(MediaTypes.`application/json`, e.toString)
            complete(StatusCodes.BadRequest, entity)
        }
      }
    }
  }

}
