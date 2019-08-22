package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.domain.model.user.{ Age, Name, UserId }

object LoadTestProtocol {

  sealed trait CommandRequest
  case class AddUserCommandRequest(name: Name, age: Age) extends CommandRequest

  sealed trait CommandResponse
  sealed trait AddUserCommandResponse extends CommandResponse

  case class AddUserCommandSucceeded(id: UserId) extends AddUserCommandResponse
  case class AddUserCommandFailed(error: String) extends AddUserCommandResponse

}
