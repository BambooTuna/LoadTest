package com.github.BambooTuna.LoadTest.boot.server

import akka.http.scaladsl.model.HttpMethods.{ GET, POST, PUT }
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.server.Directives._
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.SlickProfile

object Routes {

  val logger = LoggerFactory.getLogger(getClass)

  def createRouter(client: SlickProfile)(implicit materializer: ActorMaterializer): Router =
    commonRouter + mainRouter(client)

  def commonRouter(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET, "", CommonRoute().top),
      route(GET, "ping", CommonRoute().ping)
    )

  def mainRouter(client: SlickProfile)(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET, "user" / "get", GetUserRoute().route),
      route(POST, "user" / "add", AddUserRoute(client).route),
      route(PUT, "user" / "update", EditUserRoute().route)
    )

}
