package com.github.BambooTuna.LoadTest.adaptor.routes

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import akka.http.scaladsl.server.Directives.{ as, entity, extractActorSystem, extractRequestContext, onSuccess }
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import io.circe.syntax._
import io.circe.generic.auto._
import com.github.BambooTuna.LoadTest.adaptor.routes.json.{ EditUserRequestJson, EditUserResponseJson }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import scala.concurrent.Future

case class EditUserRoute(implicit materializer: ActorMaterializer) extends FailFastCirceSupport {

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { ctx =>
      entity(as[EditUserRequestJson]) { json =>
        val f = Future.successful()
        onSuccess(f) { _ =>
          val result = EditUserResponseJson()
          val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
          ctx.complete(StatusCodes.OK, entity)
        }
      }
    }
  }

}