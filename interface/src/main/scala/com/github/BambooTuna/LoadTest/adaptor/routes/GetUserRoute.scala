package com.github.BambooTuna.LoadTest.adaptor.routes

import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, entity, extractActorSystem, extractRequestContext, onSuccess}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import io.circe.syntax._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import com.github.BambooTuna.LoadTest.adaptor.routes.json.{AddUserResponseJson, GetUserRequestJson, GetUserResponseJson, UserJson}
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.SlickProfile
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc.UserRepositoryOnJDBCImpl
import com.github.BambooTuna.LoadTest.domain.model.user.UserId
import com.github.BambooTuna.LoadTest.usecase.GetUserUseCaseImpl
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol._
import monix.execution.Scheduler.Implicits.global
import akka.http.scaladsl.server.Directives._


case class GetUserRoute(client: SlickProfile)(implicit materializer: ActorMaterializer) extends FailFastCirceSupport {

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { ctx =>
      entity(as[GetUserRequestJson]) { json =>
        //TODO ここの変換は切り出す
        val getUserUseCase = new GetUserUseCaseImpl(new UserRepositoryOnJDBCImpl(client))
        val f =
          getUserUseCase
            .run(GetUserCommandRequest(UserId(json.user_id)))
            .runToFuture
        onSuccess(f) {
          case GetUserCommandSucceeded(users) =>
            val result = GetUserResponseJson(
              Some(
                UserJson(users.userId.value, users.name.value, users.age.value)
              )
            )
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            complete(StatusCodes.OK, entity)
          case GetUserCommandFailed(error_message) =>
            val result = GetUserResponseJson(None, Seq(error_message))
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            complete(StatusCodes.BadRequest, entity)
          case _ =>
            val result = GetUserResponseJson(None, Seq("unknown error"))
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            complete(StatusCodes.BadRequest, entity)
        }
      }
    }
  }

}
