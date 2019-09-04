package com.github.BambooTuna.LoadTest.gatling.test

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class PingSimulation extends Simulation with SimulationConfig {

  val scn = scenario(getClass.getName)
    .exec(
      http(getClass.getName)
        .get("/ping")
        .check(status.is(200))
    )

  setUp(
    scn.inject(
      rampUsers(gatlingUser.numOfUser).during(gatlingUser.rampDuration)
      //      rampUsersPerSec(1).to(request).during(set.seconds)
    )
  ).protocols(httpConf).maxDuration(gatlingUser.entireDuration)

}
