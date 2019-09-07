package com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.BudgetRepository
import monix.eval.Task

trait BudgetRepositoryOnJDBC extends BudgetRepository {

  def resolveById(id: Id): Task[Seq[Record]]

}
