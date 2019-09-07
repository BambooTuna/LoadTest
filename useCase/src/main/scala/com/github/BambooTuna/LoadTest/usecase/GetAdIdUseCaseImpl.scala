package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.AdIdRepository
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol._
import monix.eval.Task

case class GetAdIdUseCaseImpl(adidRepositories: GetAdIdRepositoryBalance[AdIdRepository]) extends GetAdIdUseCase {

  override def run(arg: GetAdIdCommandRequest): Task[GetAdIdCommandResponse] = {
    setResponseTimer
    (for {
      aggregate <- Task.pure(
        arg.request
      )
      r <- adidRepositories.getConnectionWithAdRequestId(arg.request).get(aggregate)
    } yield r)
      .map { result =>
        successCounterIncrement
        GetAdIdCommandSucceeded(result.get._2)
      }.onErrorHandle { ex =>
        failedCounterIncrement
        GetAdIdCommandFailed(ex.getMessage)
      }

  }

}
