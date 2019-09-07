package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.UserRepository
import com.github.BambooTuna.LoadTest.domain.model.user.UserId
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol._
import monix.eval.Task

case class AddUserUseCaseImpl(userRepositories: GetUserRepositoryBalance[UserRepository]) extends AddUserUseCase {

  override def run(arg: AddUserCommandRequest): Task[AddUserCommandResponse] = {
    setResponseTimer
    (for {
      aggregate <- Task.pure(
        (
          UserId(arg.user.device_id),
          arg.user
        )
      )
      r <- userRepositories.getConnectionWithUserId(aggregate._1).put(aggregate)
    } yield r)
      .map { result =>
        successCounterIncrement
        AddUserCommandSucceeded(UserId(result.toString))
      }.onErrorHandle { ex =>
        failedCounterIncrement
        AddUserCommandFailed(ex.getMessage)
      }

  }

}
