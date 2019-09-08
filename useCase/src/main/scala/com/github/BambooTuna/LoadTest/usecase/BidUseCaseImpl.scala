package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.domain.model.user.UserId
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol._
import monix.eval.Task
import com.github.BambooTuna.LoadTest.domain.model.ad._
import com.github.BambooTuna.LoadTest.domain.model.ad.command.{ BidRequestCommand, BidResponseCommand }
import com.github.BambooTuna.LoadTest.domain.model.budget.BudgetBalance
import com.github.BambooTuna.LoadTest.domain.setting.TimeZoneSetting
import com.github.BambooTuna.LoadTest.usecase.json._

import scala.concurrent.ExecutionContext

case class BidUseCaseImpl(getUserUseCase: GetUserUseCase,
                          addAdIdUseCase: AddAdIdUseCase,
                          getBudgetUseCase: GetBudgetUseCase,
                          getModelUseCase: GetModelUseCase,
                          winRedirectUrl: WinRedirectUrl)
    extends BidUseCase {

  override def run(arg: BidCommandRequest)(implicit system: ActorSystem,
                                           mat: Materializer): Task[BidCommandResponse] = {
    implicit val ec: ExecutionContext = mat.executionContext
    setResponseTimer
    for {
      aggregate <- Task.pure(
        UserId(arg.request.deviceId.value)
      )
      userData <- getUserUseCase.run(GetUserCommandRequest(aggregate))
      res <- userData match {
        case GetUserCommandSucceeded(user) =>
          Task.parMap4(
            Task(
              arg.request
            ),
            //TODO Inner MySQLを仕様する場合は runWithOutSide -> run
            getBudgetUseCase
              .run(
                GetBudgetCommandRequest(GetBudgetRequestJson(user.advertiser_id))
              ),
            getModelUseCase
            //TODO runWithOutSide or run
              .run(
                GetModelCommandRequest(
                  GetModelRequestJson(
                    footprint_log = user,
                    bid_log = createBidLog(arg.request)
                  )
                )
              ),
            addAdIdUseCase
              .run(
                AddAdIdCommandRequest(
                  (arg.request.id, AdvertiserId(user.advertiser_id))
                )
              )
          ) {
            case (r, GetBudgetCommandSucceeded(b), GetModelCommandSucceeded(m), _) =>
              successCounterIncrement
              BidCommandSucceeded(
                BidResponseCommand(
                  r.id,
                  BidPrice(calculateBidPrice(r.floorPrice, m.ctr), BudgetBalance(b.budget)),
                  AdvertiserId(user.advertiser_id),
                  winRedirectUrl
                )
              )
            case (_, GetBudgetCommandFailed(b), GetModelCommandSucceeded(_), _) =>
              failedCounterIncrement
              BidCommandFailed(s"GetBudgetCommandFailed: $b")
            case (_, GetBudgetCommandSucceeded(_), GetModelCommandFailed(m), _) =>
              failedCounterIncrement
              BidCommandFailed(s"GetModelCommandFailed: $m")
            case (_, GetBudgetCommandFailed(b), GetModelCommandFailed(m), _) =>
              failedCounterIncrement
              BidCommandFailed(s"GetBudgetCommandFailed: $b, GetModelCommandFailed: $m")
          }
        case GetUserCommandFailed(v) =>
          Task.pure {
            failedCounterIncrement
            BidCommandFailed(s"GetUserCommandFailed: $v")
          }
      }
    } yield res

  }

  private def createBidLog(r: BidRequestCommand): BidLogJson =
    BidLogJson(
      id = r.id.value,
      timestamp = r.timestamp.value,
      device_id = r.deviceId.value,
      banner_size = r.bannerSize.value,
      media_id = r.mediaId.value,
      os_type = r.osType.value,
      banner_position = r.bannerPosition.value,
      is_interstitial = r.isInterstitial.value,
      floor_price = r.floorPrice.value,
      click = 1
    )

  private def calculateBidPrice(cpc: FloorPrice, ctr: Double): Double = {
    //TODO
    120 * ctr * 1000
  }

}
