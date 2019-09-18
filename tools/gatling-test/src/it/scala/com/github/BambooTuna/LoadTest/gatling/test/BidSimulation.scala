package com.github.BambooTuna.LoadTest.gatling.test

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, MediaTypes}
import akka.stream.{ActorMaterializer, Materializer}
import com.github.BambooTuna.LoadTest.adaptor.routes.json.BidRequestRequestJson
import com.github.BambooTuna.LoadTest.usecase.json.UserInfoJson
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.circe.syntax._
import io.circe.generic.auto._
import monix.eval.Task

import scala.concurrent.ExecutionContextExecutor

class BidSimulation extends Simulation with SimulationConfig {

  implicit val system: ActorSystem                        = ActorSystem("gatling-runner")
  implicit val materializer: ActorMaterializer            = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val scn = scenario(getClass.getName)
      .exec(
        http(getClass.getName)
          .post("/bid_request")
          .headers(Map("Content-Type" -> "application/json"))
          .body(
            StringBody(
              BidRequestRequestJson(
                id = "1",
                timestamp = 1234567890L,
                device_id = "1",
                banner_size = 1,
                media_id = 1,
                os_type = 1,
                banner_position = 1,
                floor_price = 1f
              ).asJson.noSpaces
            )
          )
          .check(status.is(200))
      )

  setUp(
    scn.inject(
      rampUsers(gatlingUser.numOfUser).during(gatlingUser.rampDuration)
      //      rampUsersPerSec(1).to(request).during(set.seconds)
    )
  ).protocols(httpConf).maxDuration(gatlingUser.entireDuration)

  def initRedis(arg: UserInfoJson)(implicit system: ActorSystem,
                                   mat: Materializer): Task[HttpResponse] = {
    val request = HttpRequest(POST, s"$baseUrl/user/add")
      .withEntity(HttpEntity(MediaTypes.`application/json`, arg.asJson.noSpaces))
    Task
      .deferFutureAction { implicit ec =>
        Http()
          .singleRequest(request)
      }
  }

}