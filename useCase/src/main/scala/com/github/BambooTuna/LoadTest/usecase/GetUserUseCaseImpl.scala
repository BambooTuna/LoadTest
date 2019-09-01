package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.UserRepository
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{
  GetUserCommandFailed,
  GetUserCommandRequest,
  GetUserCommandResponse,
  GetUserCommandSucceeded
}
import monix.eval.Task

case class GetUserUseCaseImpl(userRepository: UserRepository) extends GetUserUseCase {

  override def run(arg: GetUserCommandRequest): Task[GetUserCommandResponse] = {

    (for {
      aggregate <- Task.pure(
        arg.id
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
