package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.UserInfoDao
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.UserDeviceId
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.eval.Task

case class AddUserInfoUseCase(userInfoRepositories: UserInfoRepositoryBalancer[UserInfoDao]) extends UseCaseCommon {

  def run(arg: AddUserInfoCommandRequest): Task[AddUserInfoCommandResponse] = {
    (for {
      _ <- setResponseTimer
      aggregates <- Task.pure(
        arg.userInfo
      )
      r <- Task.gather(
        aggregates.map { userInfo =>
          userInfoRepositories
            .getConnectionWithUserDeviceId(userInfo.userId)
            .insert(userInfo.userId, userInfo)
            .map(_ => userInfo.userId)
            .onErrorHandle(_ => UserDeviceId.empty)
        }
      )
    } yield r)
      .map(_.filter(_.nonEmpty))
      .responseHandle[AddUserInfoCommandResponse](AddUserInfoCommandSucceeded)(AddUserInfoCommandFailed)
  }

}

object Test {
  import monix.execution.Scheduler.Implicits.global

  def main(args: Array[String]): Unit = {

    val time = java.time.Instant.now().toEpochMilli

    val task1 = Task {
      Thread.sleep(1000)
      println(1)
      1000
    }

    val task2 = Task {
      Thread.sleep(1000)
      println(2)
      3000
    }

    val task3 = Task {
      Thread.sleep(3000)
      println(3)
      throw new Exception("")
    }.onErrorHandle(_ => 11111)

    Task
      .gather(Seq(task1, task2, task3)).runToFuture.map(m => {
        println("aaaaa" + m)
        m
      }).onComplete(c => {
        println(java.time.Instant.now().toEpochMilli - time)
        println(c)
      })

    Thread.sleep(1000000)
  }
}
