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
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{
  SetBudgetCommandFailed,
  SetBudgetCommandRequest,
  SetBudgetCommandSucceeded
}
import com.github.BambooTuna.LoadTest.usecase.SetBudgetUseCase
import com.github.BambooTuna.LoadTest.usecase.json.{ SetBudgetRequestJson, UserDataJson }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import kamon.Kamon
import kamon.metric.instrument.Gauge
import monix.execution.Scheduler.Implicits.global
import io.circe.syntax._
import io.circe.generic.auto._

case class SetBudgetRoute(setBudgetUseCase: SetBudgetUseCase)(implicit materializer: ActorMaterializer)
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
      entity(as[SetBudgetRequestJson]) { json =>
        val f =
          setBudgetUseCase
            .run(
              SetBudgetCommandRequest(json)
            )
            .runToFuture
        onSuccess(f) {
          case SetBudgetCommandSucceeded =>
            responseTime.record(java.time.Instant.now().toEpochMilli - time)
            complete(StatusCodes.OK)
          case SetBudgetCommandFailed(e) =>
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
