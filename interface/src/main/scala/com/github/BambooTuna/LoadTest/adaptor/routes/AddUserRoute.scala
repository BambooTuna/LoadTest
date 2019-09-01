package com.github.BambooTuna.LoadTest.adaptor.routes

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import akka.http.scaladsl.server.Directives.{ as, entity, extractActorSystem, extractRequestContext, onSuccess }
import akka.http.scaladsl.server.{ Route, RouteResult }
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

import scala.concurrent.Future

case class AddUserRoute(addUserUseCase: AddUserUseCase)(implicit materializer: ActorMaterializer)
    extends FailFastCirceSupport {

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { ctx =>
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
            val entity                 = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            val a: Future[RouteResult] = ctx.complete(StatusCodes.OK, entity)
            complete(StatusCodes.OK, entity)
          case AddUserCommandFailed(error_message) =>
            //TODO error時のResponseをどうするか
            val result = AddUserResponseJson(None, Seq(error_message))
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            complete(StatusCodes.BadRequest, entity)
          case _ =>
            val result = AddUserResponseJson(None, Seq("unknown error"))
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            complete(StatusCodes.BadRequest, entity)
        }
      }
    }
  }

}
