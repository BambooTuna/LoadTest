package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.UserRepository
import com.github.BambooTuna.LoadTest.domain.setting.TimeZoneSetting
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{
  GetUserCommandFailed,
  GetUserCommandRequest,
  GetUserCommandResponse,
  GetUserCommandSucceeded
}
import monix.eval.Task

case class GetUserUseCaseImpl(userRepositories: GetUserRepositoryBalance[UserRepository]) extends GetUserUseCase {

  override def run(arg: GetUserCommandRequest): Task[GetUserCommandResponse] = {
    setResponseTimer
    (for {
      aggregate <- Task.pure(
        arg.id
      )
      r <- userRepositories.getConnectionWithUserId(aggregate).get(aggregate)
    } yield r)
      .map { result =>
        successCounterIncrement
        GetUserCommandSucceeded(result.get._2)
      }.onErrorHandle { ex =>
        failedCounterIncrement
        GetUserCommandFailed(ex.getMessage)
      }

  }

}
