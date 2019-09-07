package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.AdvertiserIdRepository
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{
  AddAdIdCommandFailed,
  AddAdIdCommandRequest,
  AddAdIdCommandResponse,
  AddAdIdCommandSucceeded
}
import monix.eval.Task

case class AddAdIdUseCaseImpl(adidRepositories: GetAdvertiserIdRepositoryBalancer[AdvertiserIdRepository]) extends AddAdIdUseCase {

  override def run(arg: AddAdIdCommandRequest): Task[AddAdIdCommandResponse] = {
    setResponseTimer
    (for {
      aggregate <- Task.pure(
        arg.request
      )
      r <- adidRepositories.getConnectionWithAdRequestId(aggregate._1).put(aggregate)
    } yield r)
      .map { _ =>
        AddAdIdCommandSucceeded
      }.onErrorHandle { ex =>
        failedCounterIncrement
        AddAdIdCommandFailed(ex.getMessage)
      }

  }

}
