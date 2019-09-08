package com.github.BambooTuna.LoadTest.gatling.test

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, MediaTypes}
import akka.stream.{ActorMaterializer, Materializer}
import com.github.BambooTuna.LoadTest.adaptor.routes.json.BidRequestJson
import com.github.BambooTuna.LoadTest.domain.model.ad.AdRequestExt
import com.github.BambooTuna.LoadTest.usecase.json.UserDataJson
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.circe.syntax._
import io.circe.generic.auto._
import monix.eval.Task

import scala.concurrent.{Await, ExecutionContextExecutor, duration}
import monix.execution.Scheduler.Implicits.global

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
              BidRequestJson(
                id = "1",
                timestamp = 1234567890L,
                device_id = "1",
                banner_size = 1,
                media_id = 1,
                os_type = 1,
                banner_position = 1,
                is_interstitial = 1,
                floor_price = 1f,
                ext = AdRequestExt(1L)
              ).asJson.noSpaces
            )
          )
          .check(status.is(200))
      )

//  before {
//    Await.result(initRedis(
      UserDataJson(
        device_id = "1",
        advertiser_id = 1,
        game_install_count = 1,
        game_login_count = 1,
        game_paid_count = 1,
        game_tutorial_count = 1,
        game_extension_count = 1
      )
//    ).runToFuture, duration.Duration.Inf)
//  }

  setUp(
    scn.inject(
      rampUsers(gatlingUser.numOfUser).during(gatlingUser.rampDuration)
      //      rampUsersPerSec(1).to(request).during(set.seconds)
    )
  ).protocols(httpConf).maxDuration(gatlingUser.entireDuration)

  def initRedis(arg: UserDataJson)(implicit system: ActorSystem,
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
object AA extends App {

  print(UserDataJson(
    device_id = "1",
    advertiser_id = 1,
    game_install_count = 1,
    game_login_count = 1,
    game_paid_count = 1,
    game_tutorial_count = 1,
    game_extension_count = 1
  ).asJson.noSpaces)

}
