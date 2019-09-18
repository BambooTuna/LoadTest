package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.UserInfoRepository
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{AddUserInfoCommandRequest, AddUserInfoCommandResponse}
import monix.eval.Task

trait AddUserInfoUseCase extends UseCaseCommon {

  val userInfoRepositories: UserInfoRepositoryBalancer[UserInfoRepository]

  def run(arg: AddUserInfoCommandRequest): Task[AddUserInfoCommandResponse]

}
