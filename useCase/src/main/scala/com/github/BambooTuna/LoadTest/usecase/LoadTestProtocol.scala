package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.domain.model.user.{ Age, Name, User, UserId }

object LoadTestProtocol {

  sealed trait CommandRequest
  case class AddUserCommandRequest(name: Name, age: Age) extends CommandRequest
  case class GetUserCommandRequest(id: UserId)           extends CommandRequest

  sealed trait CommandResponse
  sealed trait AddUserCommandResponse extends CommandResponse
  sealed trait GetUserCommandResponse extends CommandResponse

  case class AddUserCommandSucceeded(id: UserId) extends AddUserCommandResponse
  case class AddUserCommandFailed(error: String) extends AddUserCommandResponse

  case class GetUserCommandSucceeded(user: User) extends GetUserCommandResponse
  case class GetUserCommandFailed(error: String) extends GetUserCommandResponse

}
