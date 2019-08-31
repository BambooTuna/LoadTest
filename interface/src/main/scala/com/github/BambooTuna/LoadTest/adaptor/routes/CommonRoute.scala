package com.github.BambooTuna.LoadTest.adaptor.routes

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Route
import io.circe.syntax._
import io.circe.generic.auto._
import com.github.BambooTuna.LoadTest.adaptor.routes.json.PongResponseJson

case class CommonRoute() {

  def top: Route = complete("Top Page!")

  def ping: Route = {
    val result = PongResponseJson("pong")
    val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
    complete(StatusCodes.OK, entity)
  }

}
