package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.UserRepository
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{ AddUserCommandRequest, AddUserCommandResponse }
import monix.eval.Task

trait AddUserUseCase {

  val userRepository: UserRepository

  def run(arg: AddUserCommandRequest): Task[AddUserCommandResponse]

}
