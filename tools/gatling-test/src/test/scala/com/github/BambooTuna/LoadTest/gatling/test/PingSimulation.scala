package com.github.BambooTuna.LoadTest.gatling.test

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class PingSimulation extends Simulation {

  val request = 1000 //   /s
  val set     = 60   //   セット回数

  val httpConf = http
    .baseUrl("http://localhost:8080")

  val scn = scenario("PingSimulation")
    .exec(
      http("ping")
        .get("/ping")
    )

  setUp(
    scn.inject(
//      constantUsersPerSec(request).during(set.seconds)
      rampUsersPerSec(1).to(request).during(set.seconds)
    )
  ).protocols(httpConf)

}
