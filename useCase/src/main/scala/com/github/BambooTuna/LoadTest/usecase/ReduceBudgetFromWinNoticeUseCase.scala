package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.BudgetRepositoryOnRedis
import com.github.BambooTuna.LoadTest.domain.model.budget.{ BudgetEventModel, Difference }
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.eval.Task

case class ReduceBudgetFromWinNoticeUseCase(repositories: BudgetRepositoryBalancer[BudgetRepositoryOnRedis])(
    getAdvertiserIdUseCase: GetAdvertiserIdUseCase
) extends UseCaseCommon {

  def run(arg: ReduceBudgetFromWinNoticeCommandRequest): Task[ReduceBudgetFromWinNoticeCommandResponse] = {
    (for {
      idAggregate <- Task.pure(
        arg.bidRequestId
      )
      recodeAggregate <- Task.pure(
        BudgetEventModel(
          Difference,
          arg.usedPrice
        )
      )
      getAdvertiserIdRes <- getAdvertiserIdUseCase.run(GetAdvertiserIdCommandRequest(idAggregate))
      r <- getAdvertiserIdRes match {
        case GetAdvertiserIdCommandSucceeded(advertiserId) =>
          repositories.getConnectionWithAdvertiserId(advertiserId).insert(advertiserId, recodeAggregate)
        case GetAdvertiserIdCommandFailed(e) =>
          Task.raiseError(new Exception(e))
      }
    } yield r)
      .map { _ =>
        ReduceBudgetFromWinNoticeCommandSucceeded
      }.onErrorHandle { ex =>
        ReduceBudgetFromWinNoticeCommandFailed(ex.getMessage)
      }
  }

}
