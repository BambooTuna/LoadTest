package com.github.BambooTuna.LoadTest.boot.server

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.github.BambooTuna.LoadTest.adaptor.json.UserResponseJson
import com.github.BambooTuna.adaptor.json.UserRequestJson
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import kamon.Kamon
import kamon.akka.http.KamonTraceDirectives
import org.slf4j.LoggerFactory

object Routes extends FailFastCirceSupport with KamonTraceDirectives {

  val logger = LoggerFactory.getLogger(getClass)

  val root =
    pathSingleSlash {
      get {
        extractUri { uri =>
          Kamon.metrics.counter("get").increment()
          complete(uri.toString())
        }
      }
    }

  val ping =
    path("ping") {
      get {
        complete("pong")
      }
    }

  val `json-test` =
    path("json") {
      post {
        entity(as[UserRequestJson]) { request =>
          complete(StatusCodes.OK, UserResponseJson())
        }
      }
    }

  val route: Route = root ~ ping ~ `json-test`

}
