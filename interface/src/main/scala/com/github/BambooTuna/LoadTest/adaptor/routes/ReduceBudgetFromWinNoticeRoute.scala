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
import com.github.BambooTuna.LoadTest.adaptor.routes.json.ReduceBudgetFromWinNoticeRequestJson
import com.github.BambooTuna.LoadTest.domain.model.budget.BudgetDifferencePrice
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.BidRequestId
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import com.github.BambooTuna.LoadTest.usecase._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import monix.execution.Scheduler.Implicits.global
import io.circe.generic.auto._
import monix.eval.Task

case class ReduceBudgetFromWinNoticeRoute(useCase: ReduceBudgetFromWinNoticeUseCase)(
    implicit materializer: ActorMaterializer
) extends RouteCommonSetting
    with FailFastCirceSupport {

  def route: Route = extractActorSystem { implicit system =>
    extractRequestContext { _ =>
      entity(as[ReduceBudgetFromWinNoticeRequestJson]) { json =>
        jsonParseHandle {
          val f =
            (if (json.is_click == 1) {
              useCase
                .run(
                  ReduceBudgetFromWinNoticeCommandRequest(
                    BidRequestId(json.id),
                    BudgetDifferencePrice(-120) //TODO
                  )
                )
            } else {
              Task(ReduceBudgetFromWinNoticeCommandNoClick)
            }).runToFuture
          onSuccess(f) {
            case ReduceBudgetFromWinNoticeCommandSucceeded =>
              complete(StatusCodes.OK)
            case ReduceBudgetFromWinNoticeCommandNoClick =>
              complete(StatusCodes.NoContent)
            case ReduceBudgetFromWinNoticeCommandFailed(e) =>
              val entity = HttpEntity(MediaTypes.`application/json`, e.toString)
              complete(StatusCodes.BadRequest, entity)
            case e =>
              val entity = HttpEntity(MediaTypes.`application/json`, e.toString)
              complete(StatusCodes.BadRequest, entity)
          }
        } { _ =>
          val entity = HttpEntity(MediaTypes.`application/json`, "json parse error!")
          complete(StatusCodes.MisdirectedRequest, entity)
        }
      }
    }
  }

}
