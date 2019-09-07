package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.BudgetRepositoryOnRedis
import com.github.BambooTuna.LoadTest.domain.model.ad.AdvertiserId
import com.github.BambooTuna.LoadTest.domain.model.budget.{
  Absolute,
  BudgetDifferencePrice,
  BudgetEventModel,
  Difference
}
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol._
import monix.eval.Task

case class SetBudgetUseCaseImpl(budgetRepositoriesOnRedis: GetBudgetRepositoryBalance[BudgetRepositoryOnRedis])
    extends SetBudgetUseCase {

  override def run(arg: SetBudgetCommandRequest): Task[SetBudgetCommandResponse] = {
    setResponseTimer
    (for {
      aggregate <- Task.pure(
        (
          AdvertiserId(arg.request.advertiser_id),
          BudgetEventModel(
            if (arg.request.event_type == 0) Absolute else Difference,
            BudgetDifferencePrice(arg.request.price)
          )
        )
      )
      r <- budgetRepositoriesOnRedis.getConnectionWithAdvertiserId(aggregate._1).put(aggregate)
    } yield r)
      .map { _ =>
        successCounterIncrement
        SetBudgetCommandSucceeded
      }.onErrorHandle { ex =>
        failedCounterIncrement
        SetBudgetCommandFailed(ex.getMessage)
      }

  }

}
