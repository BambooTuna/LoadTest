package com.github.BambooTuna.LoadTest.adaptor.routes

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Route
import io.circe.syntax._
import io.circe.generic.auto._
import com.github.BambooTuna.LoadTest.adaptor.routes.json.PongResponseJson
import kamon.Kamon

case class CommonRoute() extends RouteCommonSetting {

  def top: Route = complete(StatusCodes.OK, "Top Page!")

  def ping: Route = {
    val time   = java.time.Instant.now().toEpochMilli
    val result = PongResponseJson("pong")
    val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
    complete(StatusCodes.OK, entity)
  }

}
