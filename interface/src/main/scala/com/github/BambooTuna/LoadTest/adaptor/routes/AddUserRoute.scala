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

import com.github.BambooTuna.LoadTest.usecase.AddUserInfoUseCase
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import com.github.BambooTuna.LoadTest.usecase.json.UserDataJson
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import kamon.Kamon
import kamon.metric.instrument.Gauge
import monix.execution.Scheduler.Implicits.global
import io.circe.syntax._
import io.circe.generic.auto._

case class AddUserRoute(addUserUseCase: AddUserInfoUseCase)(implicit materializer: ActorMaterializer)
    extends FailFastCirceSupport {

  val successCounter   = Kamon.metrics.counter(this.getClass.getName + "-success")
  val noContentCounter = Kamon.metrics.counter(this.getClass.getName + "-noContent")
  val errorCounter     = Kamon.metrics.counter(this.getClass.getName + "-error")
  val responseTime =
    Kamon.metrics.gauge(this.getClass.getName + "-responseTime")(Gauge.functionZeroAsCurrentValueCollector(() => 0L))

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { _ =>
      val time = java.time.Instant.now().toEpochMilli
      successCounter.increment()
      entity(as[UserDataJson]) { json =>
        val f =
          addUserUseCase
            .run(
              AddUserCommandRequest(json)
            )
            .runToFuture
        onSuccess(f) {
          case AddUserCommandSucceeded(response) =>
            val result = response
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            responseTime.record(java.time.Instant.now().toEpochMilli - time)
            complete(StatusCodes.OK, entity)
          case AddUserCommandFailed(e) =>
            val entity = HttpEntity(MediaTypes.`application/json`, e)
            noContentCounter.increment()
            responseTime.record(java.time.Instant.now().toEpochMilli - time)
            complete(StatusCodes.BadRequest, entity)
          case e =>
            val entity = HttpEntity(MediaTypes.`application/json`, e.toString)
            errorCounter.increment()
            responseTime.record(java.time.Instant.now().toEpochMilli - time)
            complete(StatusCodes.BadRequest, entity)
        }
      }
    }
  }

}
