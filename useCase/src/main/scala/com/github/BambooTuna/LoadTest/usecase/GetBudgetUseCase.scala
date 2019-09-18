package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.BudgetRepositoryOnRedis
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{ GetBudgetCommandRequest, GetBudgetCommandResponse }
import monix.eval.Task

trait GetBudgetUseCase extends UseCaseCommon {

  val budgetRepositories: BudgetRepositoryBalancer[BudgetRepositoryOnRedis]

  def run(arg: GetBudgetCommandRequest): Task[GetBudgetCommandResponse]

  def runWithOtherServer(arg: GetBudgetCommandRequest)(implicit system: ActorSystem,
                                                   mat: Materializer): Task[GetBudgetCommandResponse] = ???

}
