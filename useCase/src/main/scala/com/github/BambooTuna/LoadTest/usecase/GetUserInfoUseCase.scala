package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.UserInfoDao
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{
  GetUserInfoCommandFailed,
  GetUserInfoCommandRequest,
  GetUserInfoCommandResponse,
  GetUserInfoCommandSucceeded
}
import monix.eval.Task

case class GetUserInfoUseCase(userInfoRepositories: UserInfoRepositoryBalancer[UserInfoDao]) extends UseCaseCommon {

  def run(arg: GetUserInfoCommandRequest): Task[GetUserInfoCommandResponse] = {
    (for {
      _ <- setResponseTimer
      aggregate <- Task.pure(
        arg.deviceId
      )
      r <- userInfoRepositories.getConnectionWithUserDeviceId(aggregate).resolveById(aggregate)
    } yield r)
      .map(_.get)
      .responseHandle[GetUserInfoCommandResponse](GetUserInfoCommandSucceeded)(GetUserInfoCommandFailed)
  }

}
