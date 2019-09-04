package com.github.BambooTuna.LoadTest.gatling.test

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

trait SimulationConfig { self: Simulation =>

  val config = ConfigFactory.load()

  val baseUrl = config.getString("reference.endpoints.url")

  val httpConf: HttpProtocolBuilder =
    http
      .baseUrl(baseUrl)
      .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val gatlingUser = new {
    val numOfUser      = config.getInt("reference.users")
    val rampDuration   = config.getDuration("reference.ramp-duration").toMillis.millis
    val holdDuration   = config.getDuration("reference.hold-duration").toMillis.millis
    val entireDuration = rampDuration + holdDuration
  }

}
