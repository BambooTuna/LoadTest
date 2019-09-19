package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.eval.Task
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad._

import scala.concurrent.ExecutionContext

case class BidRequestUseCase(getUserInfoUseCase: GetUserInfoUseCase,
                             getBudgetUseCase: GetBudgetUseCase,
                             getModelUseCase: GetModelUseCase,
                             associateBidRequestIdAndAdvertiserIdUseCase: AssociateBidRequestIdAndAdvertiserIdUseCase,
                             winRedirectUrl: WinNoticeEndpoint)(implicit system: ActorSystem, mat: Materializer)
    extends UseCaseCommon {

  implicit val ec: ExecutionContext = mat.executionContext

  def run(arg: BidRequestCommandRequest): Task[BidRequestCommandResponse] = {
    for {
      aggregate <- Task.pure(
        UserDeviceId(arg.deviceId.value)
      )
      userData <- getUserInfoUseCase.run(GetUserInfoCommandRequest(aggregate))
      res <- userData match {
        case GetUserInfoCommandSucceeded(user) =>
          Task.parMap4(
            Task(
              arg
            ),
            //TODO Inner MySQLを仕様する場合は runWithOutSide -> run
            getBudgetUseCase
              .run(
                GetBudgetCommandRequest(user.advertiserId)
              ),
            getModelUseCase
            //TODO runWithOutSide or run
              .run(
                GetModelCommandRequest(user)
              ),
            associateBidRequestIdAndAdvertiserIdUseCase
              .run(
                AssociateBidRequestIdAndAdvertiserIdCommandRequest(arg.id, user.advertiserId)
              )
          ) {
            case (r, GetBudgetCommandSucceeded(b), GetModelCommandSucceeded(m), _) =>
              BidRequestCommandSucceeded(
                r.id,
                BidPrice(calculateBidPrice(m)),
                user.advertiserId,
                winRedirectUrl
              )
            case (_, GetBudgetCommandFailed(b), GetModelCommandSucceeded(_), _) =>
              BidRequestCommandFailed(s"GetBudgetCommandFailed: $b")
            case (_, GetBudgetCommandSucceeded(_), GetModelCommandFailed(m), _) =>
              BidRequestCommandFailed(s"GetModelCommandFailed: $m")
            case (_, GetBudgetCommandFailed(b), GetModelCommandFailed(m), _) =>
              BidRequestCommandFailed(s"GetBudgetCommandFailed: $b, GetModelCommandFailed: $m")
          }
        case GetUserInfoCommandFailed(v) =>
          Task.pure {
            BidRequestCommandFailed(s"GetUserCommandFailed: $v")
          }
      }
    } yield res

  }

  private def calculateBidPrice(ctr: ClickThroughRate): Double = {
    //TODO
    120 * ctr.value * 1000
  }

}
