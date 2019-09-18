package com.github.BambooTuna.LoadTest.adaptor.routes

import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, entity, extractActorSystem, extractRequestContext, onSuccess, _}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import io.circe.syntax._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import com.github.BambooTuna.LoadTest.adaptor.routes.json.{BidRequestRequestJson, BidResponseJson}
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad._
import com.github.BambooTuna.LoadTest.domain.setting.TimeZoneSetting
import com.github.BambooTuna.LoadTest.usecase.BidRequestUseCase
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.execution.Scheduler.Implicits.global
import kamon.Kamon
import kamon.metric.instrument.Gauge
import org.slf4j.LoggerFactory

case class BidRequestRoute(bidRequestUseCase: BidRequestUseCase)(implicit materializer: ActorMaterializer) extends FailFastCirceSupport {

  val logger = LoggerFactory.getLogger(getClass)

  val successCounter   = Kamon.metrics.counter(this.getClass.getName + "-success")
  val noContentCounter = Kamon.metrics.counter(this.getClass.getName + "-noContent")
  val errorCounter     = Kamon.metrics.counter(this.getClass.getName + "-error")
  val responseTime =
    Kamon.metrics.gauge(this.getClass.getName + "-responseTime")(Gauge.functionZeroAsCurrentValueCollector(() => 0L))

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { _ =>
      val time = java.time.Instant.now().toEpochMilli
      successCounter.increment()
      entity(as[BidRequestRequestJson]) { json =>
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
            val result = BidResponseJson(
              res.id.value,
              res.bidPrice.value,
              res.advertiserId.value,
              res.winUrl.value
            )
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            responseTime.record(java.time.Instant.now().toEpochMilli - time)
            logger.debug("BidCommandSucceeded")
            complete(StatusCodes.OK, entity)
          case BidRequestCommandFailed(e) =>
            val entity = HttpEntity(MediaTypes.`application/json`, e)
            noContentCounter.increment()
            responseTime.record(java.time.Instant.now().toEpochMilli - time)
            logger.debug("BidCommandFailed")
            complete(StatusCodes.NoContent, entity)
          case e =>
            val entity = HttpEntity(MediaTypes.`application/json`, e.toString)
            errorCounter.increment()
            responseTime.record(java.time.Instant.now().toEpochMilli - time)
            logger.debug("UnKnown")
            complete(StatusCodes.NoContent, entity)
        }
      }
    }
  }

}
