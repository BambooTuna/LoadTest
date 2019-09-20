import akka.http.scaladsl.model.StatusCode

case class Response(statusCode: StatusCode, body: String)

case class BidRequestRequest(
                              id: String,
                              timestamp: Long,
                              device_id: String,
                              banner_size: Int,
                              media_id: Int,
                              os_type: Int,
                              banner_position: Int,
                              floor_price: Double,
                            )
case class BidRequestResponse(
                               id: String,
                               bid_price: Double,
                               advertiser_id: Int,
                               nurl: String
                             )

case class ReduceBudgetFromWinNoticeRequest(
                                             id: String,
                                             price: Double,
                                             is_click: Int //0 or 1
                                           )

case class ErrorResponseJson(error_code: String, message: String)