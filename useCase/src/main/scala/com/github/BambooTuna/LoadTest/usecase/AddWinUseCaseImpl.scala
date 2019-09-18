package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.BudgetRepository
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.BidRequestId
import com.github.BambooTuna.LoadTest.domain.model.budget.{ BudgetDifferencePrice, BudgetEventModel, Difference }
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.eval.Task

case class AddWinUseCaseImpl(budgetRepository: BudgetRepository, getAdIdUseCase: GetAdvertiserIdUseCase) extends AddWinUseCase {

  override def run(arg: AddWinCommandRequest): Task[AddWinCommandResponse] = {
    setResponseTimer
    (for {
      aggregate <- {
        //TODO
        if (arg.request.is_click == 1) {
          Task.pure {
            successCounterIncrement
            BidRequestId(arg.request.id)
          }
        } else {
          Task.raiseError(new Exception("no_click!"))
        }
      }
      advertiserId <- getAdIdUseCase.run(GetAdvertiserIdCommandRequest(aggregate))
      r <- advertiserId match {
        case GetAdvertiserIdCommandSucceeded(v) =>
          budgetRepository.put(
            (
              v,
              BudgetEventModel(
                Difference,
                BudgetDifferencePrice(120)
              )
            )
          )
        case GetAdvertiserIdCommandFailed(e) =>
          Task.raiseError(new Exception("GetAdvertiserIdCommandFailed"))
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
