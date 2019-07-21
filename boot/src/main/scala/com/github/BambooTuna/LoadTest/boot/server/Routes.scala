package com.github.BambooTuna.LoadTest.boot.server

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import kamon.Kamon
import kamon.akka.http.KamonTraceDirectives
import org.slf4j.LoggerFactory

object Routes extends KamonTraceDirectives {

  val logger = LoggerFactory.getLogger(getClass)

  val root: Route =
  traceName("get:request") {
    get {
      extractUri { uri =>
        logger.debug(uri.toString())
        Kamon.metrics.counter("get2").increment(10L)
        complete(uri.toString())
      }
    }
  } ~ put {
    logger.debug("Put Request")
    complete("PUT")
  }

}
