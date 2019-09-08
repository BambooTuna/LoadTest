package com.github.BambooTuna.LoadTest.usecase.calculate

import com.github.BambooTuna.LoadTest.domain.model.ad.AdvertiserId
import com.github.BambooTuna.LoadTest.usecase.json.{ GetModelRequestJson, GetModelResponseJson }
import monix.eval.Task

case class CalculateModelUseCaseImpl() extends CalculateModelUseCase {

  override def run(arg: GetModelRequestJson): Task[GetModelResponseJson] = Task {
    arg.bid_log
    arg.footprint_log

    val modelArray: List[Double] = getArray(AdvertiserId(arg.footprint_log.advertiser_id))
    val z: Double                = 0
    val result: Double           = 1 / (1 + scala.math.exp(-z))

    //TODO
    GetModelResponseJson(result)
  }

  case class ModelArray(r: List[Double]) {
//    require(r.size )
  }
  def getArray(advertiserId: AdvertiserId): List[Double] = advertiserId match {
    case AdvertiserId(1)  => List(1)
    case AdvertiserId(2)  => List(1)
    case AdvertiserId(3)  => List(1)
    case AdvertiserId(4)  => List(1)
    case AdvertiserId(5)  => List(1)
    case AdvertiserId(6)  => List(1)
    case AdvertiserId(7)  => List(1)
    case AdvertiserId(7)  => List(1)
    case AdvertiserId(8)  => List(1)
    case AdvertiserId(9)  => List(1)
    case AdvertiserId(10) => List(1)
    case AdvertiserId(11) => List(1)
    case AdvertiserId(12) => List(1)
    case AdvertiserId(13) => List(1)
    case AdvertiserId(14) => List(1)
    case AdvertiserId(15) => List(1)
    case AdvertiserId(16) => List(1)
    case AdvertiserId(17) => List(1)
    case AdvertiserId(18) => List(1)
    case AdvertiserId(19) => List(1)
    case AdvertiserId(20) => List(1)
  }

}
