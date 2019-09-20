package com.github.BambooTuna.LoadTest.adaptor.routes

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import akka.http.scaladsl.server.Directives.{ as, entity, extractActorSystem, extractRequestContext, onSuccess, _ }
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import io.circe.syntax._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import com.github.BambooTuna.LoadTest.adaptor.routes.json.{ BidRequestRequestJson, BidRequestResponseJson }
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad._
import com.github.BambooTuna.LoadTest.domain.setting.TimeZoneSetting
import com.github.BambooTuna.LoadTest.usecase.BidRequestUseCase
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.execution.Scheduler.Implicits.global

case class BidRequestRoute(bidRequestUseCase: BidRequestUseCase)(implicit materializer: ActorMaterializer)
    extends RouteCommonSetting
    with FailFastCirceSupport {

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { _ =>
      entity(as[BidRequestRequestJson]) { json =>
        jsonParseHandle {
          val f =
            bidRequestUseCase
              .run(
                BidRequestCommandRequest(
                  BidRequestId(json.id),
                  BidRequestDate(json.timestamp),
                  UserDeviceId(json.device_id),
                  BannerSize(json.banner_size),
                  MediaId(json.media_id),
                  OsType(json.os_type),
                  BannerPosition(json.banner_position),
                  FloorPrice(json.floor_price),
                )
              )
              .timeout(TimeZoneSetting.timeout)
              .runToFuture
          onSuccess(f) {
            case res: BidRequestCommandSucceeded =>
              val result = BidRequestResponseJson(
                res.id.value,
                res.bidPrice.value,
                res.advertiserId.value,
                res.winUrl.value
              )
              val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
              complete(StatusCodes.OK, entity)
            case BidRequestCommandFailed(e) =>
              val entity = HttpEntity(MediaTypes.`application/json`, e)
              complete(StatusCodes.NoContent, entity)
            case e =>
              val entity = HttpEntity(MediaTypes.`application/json`, e.toString)
              complete(StatusCodes.NoContent, entity)
          }
        } { _ =>
          val entity = HttpEntity(MediaTypes.`application/json`, "json parse error!")
          complete(StatusCodes.MisdirectedRequest, entity)
        }
      }
    }
  }

}
