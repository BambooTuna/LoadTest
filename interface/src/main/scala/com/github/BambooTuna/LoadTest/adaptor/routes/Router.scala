package com.github.BambooTuna.LoadTest.adaptor.routes

import akka.http.scaladsl.model.HttpMethod
import akka.http.scaladsl.server.{ PathMatcher, Route }
import akka.http.scaladsl.server.Directives._

object Router {

  def apply(routes: Route*): Router = new Router(routes)

}

class Router(val routes: Seq[Route]) {

  def create: Route =
    routes.reduce(_ ~ _)

  def +(router: Router): Router =
    new Router(this.routes ++ router.routes)

}

object route {

  def apply(m: HttpMethod, p: PathMatcher[Unit], route: Route): Route =
    path(p) {
      method(m)(route)
    }

}
