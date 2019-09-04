package com.github.BambooTuna.LoadTest.gatling.test

import com.github.BambooTuna.LoadTest.adaptor.routes.json.GetUserRequestJson
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import io.circe.syntax._
import io.circe.generic.auto._

class GetUserSimulation extends Simulation with SimulationConfig {

  val scn = scenario(getClass.getName)
    .exec(
      http(getClass.getName)
        .get("/user/get")
        .headers(Map("Content-Type" -> "application/json"))
        .body(
          StringBody(
            GetUserRequestJson(1L).asJson.noSpaces
          )
        )
        .check(status.is(204))
    )

  setUp(
    scn.inject(
      rampUsers(gatlingUser.numOfUser).during(gatlingUser.rampDuration)
//      rampUsersPerSec(1).to(request).during(set.seconds)
    )
  ).protocols(httpConf).maxDuration(gatlingUser.entireDuration)

}
