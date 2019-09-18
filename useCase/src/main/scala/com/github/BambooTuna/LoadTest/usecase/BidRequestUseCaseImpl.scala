package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.eval.Task
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad._

import scala.concurrent.ExecutionContext

case class BidRequestUseCaseImpl(getUserUseCase: GetUserInfoUseCase,
                                 getBudgetUseCase: GetBudgetUseCase,
                                 getModelUseCase: GetModelUseCase,
                                 winRedirectUrl: WinNoticeEndpoint)
    extends BidRequestUseCase {

  override def run(arg: BidRequestCommandRequest)(implicit system: ActorSystem,
                                           mat: Materializer): Task[BidRequestCommandResponse] = {
    implicit val ec: ExecutionContext = mat.executionContext
    setResponseTimer
    for {
      aggregate <- Task.pure(
        UserDeviceId(arg.deviceId.value)
      )
      userData <- getUserUseCase.run(GetUserInfoCommandRequest(aggregate))
      res <- userData match {
        case GetUserInfoCommandSucceeded(user) =>
          Task.parMap3(
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
            )
          ) {
            case (r, GetBudgetCommandSucceeded(b), GetModelCommandSucceeded(m)) =>
              BidRequestCommandSucceeded(
                r.id,
                BidPrice(calculateBidPrice(m)),
                user.advertiserId,
                winRedirectUrl
              )
            case (_, GetBudgetCommandFailed(b), GetModelCommandSucceeded(_)) =>
              failedCounterIncrement
              BidRequestCommandFailed(s"GetBudgetCommandFailed: $b")
            case (_, GetBudgetCommandSucceeded(_), GetModelCommandFailed(m)) =>
              failedCounterIncrement
              BidRequestCommandFailed(s"GetModelCommandFailed: $m")
            case (_, GetBudgetCommandFailed(b), GetModelCommandFailed(m)) =>
              failedCounterIncrement
              BidRequestCommandFailed(s"GetBudgetCommandFailed: $b, GetModelCommandFailed: $m")
          }
        case GetUserInfoCommandFailed(v) =>
          Task.pure {
            failedCounterIncrement
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
