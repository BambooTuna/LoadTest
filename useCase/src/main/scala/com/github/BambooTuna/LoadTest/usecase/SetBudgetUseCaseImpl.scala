package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.BudgetRepositoryOnRedis
import com.github.BambooTuna.LoadTest.domain.model.budget.{Absolute, BudgetDifferencePrice, BudgetEventModel}
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.eval.Task

case class SetBudgetUseCaseImpl(budgetRepositoriesOnRedis: BudgetRepositoryBalancer[BudgetRepositoryOnRedis])
    extends SetBudgetUseCase {

  override def run(arg: SetBudgetCommandRequest): Task[SetBudgetCommandResponse] = {
    (for {
      aggregate <- Task.pure(arg)
      r <- budgetRepositoriesOnRedis.getConnectionWithAdvertiserId(aggregate.advertiserId).put((
        aggregate.advertiserId,
        BudgetEventModel(Absolute, BudgetDifferencePrice(aggregate.budgetBalance.value))
      ))
    } yield r)
      .map { _ =>
        SetBudgetCommandSucceeded
      }.onErrorHandle { ex =>
        SetBudgetCommandFailed(ex.getMessage)
      }

  }

}
