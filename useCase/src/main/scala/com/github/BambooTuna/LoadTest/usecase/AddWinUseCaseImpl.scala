package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.BudgetRepository
import com.github.BambooTuna.LoadTest.domain.model.ad.AdRequestId
import com.github.BambooTuna.LoadTest.domain.model.budget.{ BudgetDifferencePrice, BudgetEventModel, Difference }
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol._
import monix.eval.Task

case class AddWinUseCaseImpl(budgetRepository: BudgetRepository, getAdIdUseCase: GetAdIdUseCase) extends AddWinUseCase {

  override def run(arg: AddWinCommandRequest): Task[AddWinCommandResponse] = {
    setResponseTimer
    (for {
      aggregate <- {
        //TODO
        if (arg.request.is_click == 1) {
          Task.pure {
            successCounterIncrement
            AdRequestId(arg.request.id)
          }
        } else {
          Task.raiseError(new Exception("no_click!"))
        }
      }
      advertiserId <- getAdIdUseCase.run(GetAdIdCommandRequest(aggregate))
      r <- advertiserId match {
        case GetAdIdCommandSucceeded(v) =>
          budgetRepository.put(
            (
              v,
              BudgetEventModel(
                Difference,
                BudgetDifferencePrice(120)
              )
            )
          )
        case GetAdIdCommandFailed(e) =>
          Task.raiseError(new Exception("GetAdIdCommandFailed"))
      }
    } yield r)
      .map { _ =>
        AddWinCommandSucceeded
      }.onErrorHandle { ex =>
        failedCounterIncrement
        AddWinCommandFailed(ex.getMessage)
      }
  }

}
