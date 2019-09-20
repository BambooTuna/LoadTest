import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import org.scalatest.{FreeSpecLike, Matchers}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

class LoadTestE2ETest extends FreeSpecLike with Matchers with ScalaFutures {

  implicit val system: ActorSystem                        = ActorSystem("e2e-test")
  implicit val materializer: ActorMaterializer            = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val pc: PatienceConfig = PatienceConfig(20 seconds, 1 seconds)

  def createUri(path: String): Uri =
    Uri.from(scheme = "http", host = "localhost", port = 8080, path = path)
  def createUri(path: String, queryString: Option[String]): Uri =
    Uri.from(scheme = "http",
      host = "localhost",
      port = 8080,
      path = path,
      queryString = queryString)

  val requestId = "1"
  val deviceId = "1"
  val advertiserId = 1

  "bit request" - {
    "200 OK" in {
      val f =
        createBidRequest(BidRequestRequest(
          id = requestId,
          timestamp = java.time.Instant.now().getEpochSecond,
          device_id = deviceId,
          banner_position = 1,
          media_id = 1,
          os_type = 1,
          banner_size = 1,
          floor_price = 1
        ))
      val response = f.futureValue
      response.statusCode shouldBe StatusCodes.OK
      val bidRequestResponse = parser.decode[BidRequestResponse](response.body).toOption.get
      bidRequestResponse.id shouldBe requestId
      bidRequestResponse.advertiser_id shouldBe advertiserId
      bidRequestResponse.nurl shouldBe "http://localhost:8080/win"
    }

    "204 NoContent when device_id is unknown" in {
      val f =
        createBidRequest(BidRequestRequest(
          id = requestId,
          timestamp = java.time.Instant.now().getEpochSecond,
          device_id = "unknown device id",
          banner_position = 1,
          media_id = 1,
          os_type = 1,
          banner_size = 1,
          floor_price = 1
        ))
      val response = f.futureValue
      response.statusCode shouldBe StatusCodes.NoContent
    }

  }

  "win notice" - {

    "204 NoContent when is-click is 0" in {
      val f =
        createReduceBudgetFromWinNoticeRequest(ReduceBudgetFromWinNoticeRequest(
          id = requestId,
          price = 100,
          is_click = 0
        ))
      val response = f.futureValue
      response.statusCode shouldBe StatusCodes.NoContent
    }

    "200 OK when is-click is 1" in {
      val f =
        createReduceBudgetFromWinNoticeRequest(ReduceBudgetFromWinNoticeRequest(
          id = requestId,
          price = 100,
          is_click = 1
        ))
      val response = f.futureValue
      response.statusCode shouldBe StatusCodes.OK
    }

    "BudRequest when request id is unknown" in {
      val f =
        createReduceBudgetFromWinNoticeRequest(ReduceBudgetFromWinNoticeRequest(
          id = "unknown request id",
          price = 100,
          is_click = 1
        ))
      val response = f.futureValue
      response.statusCode shouldBe StatusCodes.BadRequest
    }

  }


  def createBidRequest(bidRequestRequest: BidRequestRequest): Future[Response] = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = createUri(path = "/bid_request"),
      headers = commonHeaderList,
      entity = HttpEntity(ContentTypes.`application/json`, bidRequestRequest.asJson.noSpaces))
    Http().singleRequest(request).flatMap(convertHttpResponseToFutureString)
  }

  def createReduceBudgetFromWinNoticeRequest(reduceBudgetFromWinNoticeRequest: ReduceBudgetFromWinNoticeRequest): Future[Response] = {
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = createUri(path = "/win"),
      headers = commonHeaderList,
      entity = HttpEntity(ContentTypes.`application/json`, reduceBudgetFromWinNoticeRequest.asJson.noSpaces))
    Http().singleRequest(request).flatMap(convertHttpResponseToFutureString)
  }

  private val commonHeaderList: List[RawHeader] = {
    List(
      RawHeader("Content-Type", "application/json")
    )
  }

  private def convertHttpResponseToFutureString(httpResponse: HttpResponse): Future[Response] =
    Unmarshal(httpResponse.entity).to[String].map(body => Response(httpResponse.status, body))

}
