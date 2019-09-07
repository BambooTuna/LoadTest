package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.UserRepository
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{ GetUserCommandRequest, GetUserCommandResponse }
import monix.eval.Task

trait GetUserUseCase extends UseCaseCommon {

  val userRepositories: GetUserRepositoryBalance[UserRepository]

  def run(arg: GetUserCommandRequest): Task[GetUserCommandResponse]

}
