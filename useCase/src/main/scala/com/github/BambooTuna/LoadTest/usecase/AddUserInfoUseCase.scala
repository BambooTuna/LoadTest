package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.UserInfoDao
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.eval.Task

case class AddUserInfoUseCase(userInfoRepositories: UserInfoRepositoryBalancer[UserInfoDao]) extends UseCaseCommon {

  def run(arg: AddUserInfoCommandRequest): Task[AddUserInfoCommandResponse] = {
    (for {
      idAggregate <- Task.pure(
        arg.userInfo.userId
      )
      recordAggregate <- Task.pure(
        arg.userInfo
      )
      r <- userInfoRepositories.getConnectionWithUserDeviceId(idAggregate).insert(idAggregate, recordAggregate)
    } yield r)
      .map { _ =>
        AddUserInfoCommandSucceeded(arg.userInfo.userId)
      }.onErrorHandle { ex =>
        AddUserInfoCommandFailed(ex.getMessage)
      }

  }

}
