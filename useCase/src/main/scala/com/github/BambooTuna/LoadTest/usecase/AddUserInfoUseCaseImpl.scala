package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.UserInfoRepository
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.UserDeviceId
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.eval.Task

case class AddUserInfoUseCaseImpl(userInfoRepositories: UserInfoRepositoryBalancer[UserInfoRepository]) extends AddUserInfoUseCase {

  override def run(arg: AddUserInfoCommandRequest): Task[AddUserInfoCommandResponse] = {
    setResponseTimer
    (for {
      aggregate <- Task.pure(arg.userInfo)
      r <- userInfoRepositories.getConnectionWithUserDeviceId(aggregate.userId).put((aggregate.userId, aggregate))
    } yield r)
      .map { _ =>
        AddUserInfoCommandSucceeded(arg.userInfo.userId)
      }.onErrorHandle { ex =>
        AddUserInfoCommandFailed(ex.getMessage)
      }

  }

}
