package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.BudgetRepositoryOnRedis
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{ GetBudgetCommandRequest, GetBudgetCommandResponse }
import monix.eval.Task

trait GetBudgetUseCase extends UseCaseCommon {

  val budgetRepositoriesOnRedis: GetBudgetRepositoryBalance[BudgetRepositoryOnRedis]

  def run(arg: GetBudgetCommandRequest): Task[GetBudgetCommandResponse]

  def runWithOutSide(arg: GetBudgetCommandRequest)(implicit system: ActorSystem,
                                                   mat: Materializer): Task[GetBudgetCommandResponse] = ???

}
