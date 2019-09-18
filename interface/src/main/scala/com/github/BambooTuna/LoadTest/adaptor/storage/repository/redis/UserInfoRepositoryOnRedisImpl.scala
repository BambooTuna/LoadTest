package com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnRedisClient
import com.github.BambooTuna.LoadTest.domain.model.dsp.UserInfo
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.{AdvertiserId, UserDeviceId}
import com.github.BambooTuna.LoadTest.domain.model.dsp.user.GameInstallCount
import monix.eval.Task

import scala.concurrent.ExecutionContext

class UserInfoRepositoryOnRedisImpl(client: OnRedisClient) extends UserInfoRepositoryOnRedis {

  import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.JsonCodecToByteStringSerdesConversion._
  import io.circe.generic.auto._

  implicit val executionContext: ExecutionContext = client.db.executionContext

  override def resolveById(id: Id): Task[Option[Record]] =
    Task
      .deferFutureAction { implicit ec =>
        client.db.get[UserInfoJson](generateKey(id)).map(_.map(convertToAggregate))
      }

  override def insert(id: Id, record: Record): Task[Long] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db
          .set[UserInfoJson](generateKey(id), convertToJson(record), exSeconds = None)
          .map(r => if (r) 1L else 0L)
      }
  }

  private def generateKey(id: UserDeviceId): String = s"user_${id.value.toString}"

  private def convertToAggregate(json: UserInfoJson): UserInfo = {
    UserInfo(
      UserDeviceId(json.device_id),
      AdvertiserId(json.advertiser_id),
      GameInstallCount(json.game_install_count)
    )
  }

  private def convertToJson(aggregate: UserInfo): UserInfoJson = {
    UserInfoJson(
      aggregate.userId.value,
      aggregate.advertiserId.value,
      aggregate.gameInstallCount.value
    )
  }

  case class UserInfoJson(device_id: String, advertiser_id: Int, game_install_count: Long)

}
