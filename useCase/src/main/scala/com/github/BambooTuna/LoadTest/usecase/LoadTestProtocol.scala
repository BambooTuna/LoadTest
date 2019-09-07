package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.domain.model.ad.{ AdRequestId, AdvertiserId }
import com.github.BambooTuna.LoadTest.domain.model.ad.command.{ BidRequestCommand, BidResponseCommand }
import com.github.BambooTuna.LoadTest.domain.model.user.UserId
import com.github.BambooTuna.LoadTest.usecase.json._

object LoadTestProtocol {

  sealed trait CommandRequest
  case class BidCommandRequest(request: BidRequestCommand)        extends CommandRequest
  case class GetUserCommandRequest(id: UserId)                    extends CommandRequest
  case class AddUserCommandRequest(user: UserDataJson)            extends CommandRequest
  case class GetModelCommandRequest(request: GetModelRequestJson) extends CommandRequest

  case class AddWinCommandRequest(request: AddWinRequestJson) extends CommandRequest

  case class GetAdIdCommandRequest(request: AdRequestId)                 extends CommandRequest
  case class AddAdIdCommandRequest(request: (AdRequestId, AdvertiserId)) extends CommandRequest

  case class GetBudgetCommandRequest(request: GetBudgetRequestJson) extends CommandRequest
  case class SetBudgetCommandRequest(request: SetBudgetRequestJson) extends CommandRequest

  //////////////////
  sealed trait CommandResponse
  sealed trait GetUserCommandResponse  extends CommandResponse
  sealed trait AddUserCommandResponse  extends CommandResponse
  sealed trait BidCommandResponse      extends CommandResponse
  sealed trait GetModelCommandResponse extends CommandResponse
  sealed trait AddWinCommandResponse   extends CommandResponse

  sealed trait GetAdIdCommandResponse extends CommandResponse
  sealed trait AddAdIdCommandResponse extends CommandResponse

  sealed trait GetBudgetCommandResponse extends CommandResponse
  sealed trait SetBudgetCommandResponse extends CommandResponse

  //////////////////
  case class GetUserCommandSucceeded(response: UserDataJson) extends GetUserCommandResponse
  case class GetUserCommandFailed(error: String)             extends GetUserCommandResponse

  case class AddUserCommandSucceeded(response: UserId) extends AddUserCommandResponse
  case class AddUserCommandFailed(error: String)       extends AddUserCommandResponse

  case class BidCommandSucceeded(response: BidResponseCommand) extends BidCommandResponse
  case class BidCommandFailed(error: String)                   extends BidCommandResponse

  case class GetModelCommandSucceeded(response: GetModelResponseJson) extends GetModelCommandResponse
  case class GetModelCommandFailed(error: String)                     extends GetModelCommandResponse

  case object AddWinCommandSucceeded            extends AddWinCommandResponse
  case class AddWinCommandFailed(error: String) extends AddWinCommandResponse

  case class GetAdIdCommandSucceeded(response: AdvertiserId) extends GetAdIdCommandResponse
  case class GetAdIdCommandFailed(error: String)             extends GetAdIdCommandResponse

  case object AddAdIdCommandSucceeded            extends AddAdIdCommandResponse
  case class AddAdIdCommandFailed(error: String) extends AddAdIdCommandResponse

  case class GetBudgetCommandSucceeded(response: GetBudgetResponseJson) extends GetBudgetCommandResponse
  case class GetBudgetCommandFailed(error: String)                      extends GetBudgetCommandResponse

  case object SetBudgetCommandSucceeded            extends SetBudgetCommandResponse
  case class SetBudgetCommandFailed(error: String) extends SetBudgetCommandResponse

}
