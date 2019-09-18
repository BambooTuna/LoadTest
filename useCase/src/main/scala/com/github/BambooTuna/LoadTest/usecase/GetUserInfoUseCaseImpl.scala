package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.UserInfoRepository
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{
  GetUserInfoCommandFailed,
  GetUserInfoCommandRequest,
  GetUserInfoCommandResponse,
  GetUserInfoCommandSucceeded
}
import monix.eval.Task

case class GetUserInfoUseCaseImpl(userInfoRepositories: UserInfoRepositoryBalancer[UserInfoRepository]) extends GetUserInfoUseCase {

  override def run(arg: GetUserInfoCommandRequest): Task[GetUserInfoCommandResponse] = {
    setResponseTimer
    (for {
      _ <- Task.pure(
        setResponseTimer
      )
      aggregate <- Task.pure(
        arg.deviceId
      )
      r <- userInfoRepositories.getConnectionWithUserDeviceId(aggregate).get(aggregate)
    } yield r)
      .map { result =>
        GetUserInfoCommandSucceeded(result.get._2)
      }.onErrorHandle { ex =>
        failedCounterIncrement
        GetUserInfoCommandFailed(ex.getMessage)
      }
      .doOnFinish(_ => Task.pure(recodeResponseTime))

  }

}
