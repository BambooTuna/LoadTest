package com.github.BambooTuna.LoadTest.boot.server

import akka.http.scaladsl.model.HttpMethods.{ GET, POST, PUT }
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.server.Directives._

object Routes {

  val logger = LoggerFactory.getLogger(getClass)

  def createRouter(implicit materializer: ActorMaterializer): Router =
    commonRouter + mainRouter

  def commonRouter(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET, "", CommonRoute().top),
      route(GET, "ping", CommonRoute().ping)
    )

  def mainRouter(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET, "user" / "get", GetUserRoute().route),
      route(POST, "user" / "add", AddUserRoute().route),
      route(PUT, "user" / "update", EditUserRoute().route)
    )

}
