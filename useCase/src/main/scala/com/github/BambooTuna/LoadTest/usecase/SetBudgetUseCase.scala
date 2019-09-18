package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.BudgetRepositoryOnRedis
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{ SetBudgetCommandRequest, SetBudgetCommandResponse }
import monix.eval.Task

trait SetBudgetUseCase extends UseCaseCommon {

  val budgetRepositoriesOnRedis: BudgetRepositoryBalancer[BudgetRepositoryOnRedis]

  def run(arg: SetBudgetCommandRequest): Task[SetBudgetCommandResponse]

}
