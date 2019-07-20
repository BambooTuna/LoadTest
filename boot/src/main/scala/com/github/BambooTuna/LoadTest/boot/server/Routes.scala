package com.github.BambooTuna.LoadTest.boot.server

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

object Routes {

  val root: Route = get {
    extractUri { uri =>
      complete(uri.toString())
    }
  } ~ put {
    complete("PUT")
  }

}
