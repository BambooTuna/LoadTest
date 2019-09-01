package com.github.BambooTuna.LoadTest.adaptor.routes

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import akka.http.scaladsl.server.Directives.{ as, entity, extractActorSystem, extractRequestContext, onSuccess }
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import io.circe.syntax._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import com.github.BambooTuna.LoadTest.adaptor.routes.json.{ AddUserRequestJson, AddUserResponseJson, UserIdJson }
import com.github.BambooTuna.LoadTest.domain.model.user.{ Age, Name }
import com.github.BambooTuna.LoadTest.usecase.AddUserUseCase
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{
  AddUserCommandFailed,
  AddUserCommandRequest,
  AddUserCommandSucceeded
}
import monix.execution.Scheduler.Implicits.global
import akka.http.scaladsl.server.Directives._
import kamon.Kamon

case class AddUserRoute(addUserUseCase: AddUserUseCase)(implicit materializer: ActorMaterializer)
    extends FailFastCirceSupport {

  val counter      = Kamon.metrics.counter(this.getClass.getName)
  val responseTime = Kamon.metrics.histogram(this.getClass.getName + "-top")

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { _ =>
      val time = java.time.Instant.now().getEpochSecond
      counter.increment()
      entity(as[AddUserRequestJson]) { json =>
        //TODO ここの変換は切り出す
        val f =
          addUserUseCase
            .run(AddUserCommandRequest(Name(json.name), Age(json.age)))
            .runToFuture
        onSuccess(f) {
          case AddUserCommandSucceeded(id) =>
            val result = AddUserResponseJson(
              Some(
                UserIdJson(
                  id.value
                )
              )
            )
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            responseTime.record(java.time.Instant.now().getEpochSecond - time)
            complete(StatusCodes.OK, entity)
          case AddUserCommandFailed(error_message) =>
            //TODO error時のResponseをどうするか
            val result = AddUserResponseJson(None, Seq(error_message))
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            responseTime.record(java.time.Instant.now().getEpochSecond - time)
            complete(StatusCodes.BadRequest, entity)
          case _ =>
            val result = AddUserResponseJson(None, Seq("unknown error"))
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            responseTime.record(java.time.Instant.now().getEpochSecond - time)
            complete(StatusCodes.BadRequest, entity)
        }
      }
    }
  }

}
