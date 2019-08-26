package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc.UserRepositoryOnJDBC
import com.github.BambooTuna.LoadTest.domain.model.user.{ User, UserId }
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{
  AddUserCommandFailed,
  AddUserCommandRequest,
  AddUserCommandResponse,
  AddUserCommandSucceeded
}
import monix.eval.Task

import scala.util.Random

class AddUserUseCaseImpl(userRepository: UserRepositoryOnJDBC) {

  def run(arg: AddUserCommandRequest): Task[AddUserCommandResponse] = {

    val id = Random.nextLong()
    (for {

      aggregate <- Task.pure(
        User(
          UserId(id),
          arg.name,
          arg.age
        )
      )
      _ <- userRepository.put(aggregate)
    } yield aggregate.userId)
      .map { result =>
        AddUserCommandSucceeded(result)
      }.onErrorHandle { ex =>
        AddUserCommandFailed(ex.getMessage)
      }

  }

}
