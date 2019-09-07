package com.github.BambooTuna.LoadTest.usecase.command

import com.github.BambooTuna.LoadTest.domain.model.budget.BudgetBalance
import com.github.BambooTuna.LoadTest.domain.model.dsp.UserInfo
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad._

object DspCommandProtocol {

  sealed trait CommandResponse
  sealed trait BidRequestCommandResponse      extends CommandResponse
  sealed trait GetUserInfoCommandResponse      extends CommandResponse
  sealed trait GetBudgetCommandResponse      extends CommandResponse
  sealed trait GetModelCommandResponse extends CommandResponse
  sealed trait GetAdvertiserIdCommandResponse extends CommandResponse

  sealed trait AddUserInfoCommandResponse      extends CommandResponse
  sealed trait SetBudgetCommandResponse      extends CommandResponse
  /////////////////////////////
  sealed trait CommandRequest
  case class BidRequestCommandRequest(
                                       id: BidRequestId,
                                       timestamp: BidRequestDate,
                                       deviceId: UserDeviceId,
                                       bannerSize: BannerSize,
                                       mediaId: MediaId,
                                       osType: OsType,
                                       bannerPosition: BannerPosition,
                                       floorPrice: FloorPrice
                                     ) extends CommandRequest
  case class GetUserInfoCommandRequest(deviceId: UserDeviceId) extends CommandRequest
  case class GetBudgetCommandRequest(advertiserId: AdvertiserId) extends CommandRequest
  case class GetModelCommandRequest(userInfo: UserInfo) extends CommandRequest
  case class GetAdvertiserIdCommandRequest(id: BidRequestId) extends CommandRequest

  case class AddUserInfoCommandRequest(userInfo: UserInfo) extends CommandRequest
  case class SetBudgetCommandRequest(advertiserId: AdvertiserId, budgetBalance: BudgetBalance) extends CommandRequest
  ////////////////////////////////////
  case class BidRequestCommandSucceeded(
                                         id: BidRequestId,
                                         bidPrice: BidPrice,
                                         advertiserId: AdvertiserId,
                                         winUrl: WinNoticeEndpoint
                                       ) extends BidRequestCommandResponse
  case class BidRequestCommandFailed(error: String)                   extends BidRequestCommandResponse

  case class GetUserInfoCommandSucceeded(userInfo: UserInfo) extends GetUserInfoCommandResponse
  case class GetUserInfoCommandFailed(error: String)                   extends GetUserInfoCommandResponse
  case class GetBudgetCommandSucceeded(budgetBalance: BudgetBalance) extends GetBudgetCommandResponse
  case class GetBudgetCommandFailed(error: String)                   extends GetBudgetCommandResponse
  case class GetModelCommandSucceeded(ctr: ClickThroughRate) extends GetModelCommandResponse
  case class GetModelCommandFailed(error: String)                   extends GetModelCommandResponse
  case class GetAdvertiserIdCommandSucceeded(advertiserId: AdvertiserId) extends GetAdvertiserIdCommandResponse
  case class GetAdvertiserIdCommandFailed(error: String)                   extends GetAdvertiserIdCommandResponse

  case class AddUserInfoCommandSucceeded(deviceId: UserDeviceId) extends AddUserInfoCommandResponse
  case class AddUserInfoCommandFailed(error: String)                   extends AddUserInfoCommandResponse
  case object SetBudgetCommandSucceeded extends SetBudgetCommandResponse
  case class SetBudgetCommandFailed(error: String)                   extends SetBudgetCommandResponse
  ////////////
//  sealed trait AddWinCommandResponse   extends CommandResponse
//
//  sealed trait AddAdIdCommandResponse extends CommandResponse
//
//  sealed trait SetBudgetCommandResponse extends CommandResponse
//
//  case class AddWinCommandRequest(request: AddWinRequestJson) extends CommandRequest
//
//  case class AddAdIdCommandRequest(request: (BidRequestId, AdvertiserId)) extends CommandRequest
//
//  case class SetBudgetCommandRequest(request: SetBudgetRequestJson) extends CommandRequest
//
//  case object AddWinCommandSucceeded            extends AddWinCommandResponse
//  case class AddWinCommandFailed(error: String) extends AddWinCommandResponse
//
//  case object AddAdIdCommandSucceeded            extends AddAdIdCommandResponse
//  case class AddAdIdCommandFailed(error: String) extends AddAdIdCommandResponse
//
//  case object SetBudgetCommandSucceeded            extends SetBudgetCommandResponse
//  case class SetBudgetCommandFailed(error: String) extends SetBudgetCommandResponse

}
