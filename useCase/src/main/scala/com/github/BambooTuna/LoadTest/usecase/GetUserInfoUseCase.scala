package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.UserInfoRepository
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{GetUserInfoCommandRequest, GetUserInfoCommandResponse}
import monix.eval.Task

trait GetUserInfoUseCase extends UseCaseCommon {

  val userInfoRepositories: UserInfoRepositoryBalancer[UserInfoRepository]

  def run(arg: GetUserInfoCommandRequest): Task[GetUserInfoCommandResponse]

}
