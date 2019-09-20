package com.github.BambooTuna.LoadTest.adaptor.routes

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import akka.http.scaladsl.server.Directives.{
  as,
  complete,
  entity,
  extractActorSystem,
  extractRequestContext,
  onSuccess
}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes.json.{ GetBudgetRequestJson, GetBudgetResponseJson }
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.AdvertiserId
import com.github.BambooTuna.LoadTest.usecase.GetBudgetUseCase
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import monix.execution.Scheduler.Implicits.global

import io.circe.syntax._
import io.circe.generic.auto._

case class GetBudgetRoute(getBudgetUseCase: GetBudgetUseCase)(implicit materializer: ActorMaterializer)
    extends RouteCommonSetting
    with FailFastCirceSupport {

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { _ =>
      entity(as[GetBudgetRequestJson]) { json =>
        val f =
          getBudgetUseCase
            .run(
              GetBudgetCommandRequest(
                AdvertiserId(json.advertiser_id)
              )
            )
            .runToFuture
        onSuccess(f) {
          case GetBudgetCommandSucceeded(response) =>
            val result = GetBudgetResponseJson(response.value)
            val entity = HttpEntity(MediaTypes.`application/json`, result.asJson.noSpaces)
            complete(StatusCodes.OK, entity)
          case GetBudgetCommandFailed(e) =>
            val entity = HttpEntity(MediaTypes.`application/json`, e)
            complete(StatusCodes.BadRequest, entity)
          case e =>
            val entity = HttpEntity(MediaTypes.`application/json`, e.toString)
            complete(StatusCodes.BadRequest, entity)
        }
      }
    }
  }

}
