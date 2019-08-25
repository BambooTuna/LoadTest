package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc.UserRepositoryOnJDBC
import com.github.BambooTuna.LoadTest.domain.model.user.UserId
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{
  GetUserCommandFailed,
  GetUserCommandRequest,
  GetUserCommandResponse,
  GetUserCommandSucceeded
}
import monix.eval.Task

import scala.util.Random

class GetUserUseCaseImpl(userRepository: UserRepositoryOnJDBC) {

  def run(arg: GetUserCommandRequest): Task[GetUserCommandResponse] = {

    val id = Random.nextLong()
    (for {
      aggregate <- Task.pure(
        UserId(id)
      )
      r <- userRepository.get(aggregate)
    } yield r)
      .map { result =>
        GetUserCommandSucceeded(result.get)
      }.onErrorHandle { ex =>
        GetUserCommandFailed(ex.getMessage)
      }

  }

}
