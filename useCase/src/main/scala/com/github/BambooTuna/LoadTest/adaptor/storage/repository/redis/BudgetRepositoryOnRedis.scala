package com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.BudgetRepository
import com.github.BambooTuna.LoadTest.domain.model.budget.BudgetBalance
import monix.eval.Task

trait BudgetRepositoryOnRedis extends BudgetRepository {

  def resolveById(id: Id): Task[BudgetBalance]

}
