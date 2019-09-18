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
import com.github.BambooTuna.LoadTest.domain.model.budget.BudgetBalance
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.AdvertiserId
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{
  SetBudgetCommandFailed,
  SetBudgetCommandRequest,
  SetBudgetCommandSucceeded
}
import com.github.BambooTuna.LoadTest.usecase.SetBudgetUseCase
import com.github.BambooTuna.LoadTest.usecase.json.SetBudgetRequestJson
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import monix.execution.Scheduler.Implicits.global
import io.circe.generic.auto._

case class SetBudgetRoute(setBudgetUseCase: SetBudgetUseCase)(implicit materializer: ActorMaterializer)
    extends RouteCommonSetting
    with FailFastCirceSupport {

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { _ =>
      entity(as[SetBudgetRequestJson]) { json =>
        val f =
          setBudgetUseCase
            .run(
              SetBudgetCommandRequest(
                AdvertiserId(json.advertiser_id),
                BudgetBalance(json.price)
              )
            )
            .runToFuture
        onSuccess(f) {
          case SetBudgetCommandSucceeded =>
            complete(StatusCodes.OK)
          case SetBudgetCommandFailed(e) =>
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
