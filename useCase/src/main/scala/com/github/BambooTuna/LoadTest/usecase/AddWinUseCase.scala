package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.BudgetRepository
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{ AddWinCommandRequest, AddWinCommandResponse }
import monix.eval.Task

trait AddWinUseCase extends UseCaseCommon {

  val budgetRepository: BudgetRepository

  def run(arg: AddWinCommandRequest): Task[AddWinCommandResponse]

}
