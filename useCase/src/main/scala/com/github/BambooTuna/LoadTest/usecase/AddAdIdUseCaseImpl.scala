package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.AdIdRepository
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{
  AddAdIdCommandFailed,
  AddAdIdCommandRequest,
  AddAdIdCommandResponse,
  AddAdIdCommandSucceeded
}
import monix.eval.Task

case class AddAdIdUseCaseImpl(adidRepositories: GetAdIdRepositoryBalance[AdIdRepository]) extends AddAdIdUseCase {

  override def run(arg: AddAdIdCommandRequest): Task[AddAdIdCommandResponse] = {
    setResponseTimer
    (for {
      aggregate <- Task.pure(
        arg.request
      )
      r <- adidRepositories.getConnectionWithAdRequestId(aggregate._1).put(aggregate)
    } yield r)
      .map { _ =>
        successCounterIncrement
        AddAdIdCommandSucceeded
      }.onErrorHandle { ex =>
        failedCounterIncrement
        AddAdIdCommandFailed(ex.getMessage)
      }

  }

}
