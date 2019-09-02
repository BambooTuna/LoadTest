package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.domain.model.user.{Age, Name, UserId}
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{AddUserCommandRequest, AddUserCommandSucceeded}
import monix.execution.CancelableFuture
import monix.execution.Scheduler.Implicits.global
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpecLike, Matchers}

class AddUserUseCaseImplSpec()
  extends FreeSpecLike
    with Matchers
    with ScalaFutures
{
  val userRepositoryMock = new UserRepositoryOnJDBCMock
  val addUserUseCaseImpl = new AddUserUseCaseImpl(userRepositoryMock)

  "AddUserUseCaseImplSpec" - {

    "run" in {

      val name = Name("a")
      val age = Age(1)
      val response = addUserUseCaseImpl
        .run(AddUserCommandRequest(name, age))
        .runToFuture

      response.futureValue shouldBe AddUserCommandSucceeded(UserId())

    }

  }


}
