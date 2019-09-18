package com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnRedisClient
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.{ AdvertiserId, UserDeviceId }
import monix.eval.Task

import scala.concurrent.ExecutionContext

class AdvertiserIdRepositoryOnRedisImpl(client: OnRedisClient) extends AdvertiserIdRepositoryOnRedis {

  import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.IntByteStringSerializer._
  import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.IntByteStringDeserializer._

  implicit val executionContext: ExecutionContext = client.db.executionContext

  override def resolveById(id: Id): Task[Option[Record]] =
    Task
      .deferFutureAction { implicit ec =>
        client.db.get[Int](id.value).map(_.map(convertToAggregate))
      }

  override def insert(id: Id, record: Record): Task[Long] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db
          .set[Int](id.value, convertToValue(record), exSeconds = None)
          .map(r => if (r) 1L else 0L)
      }
  }

  private def generateKey(id: UserDeviceId): String = s"adid_${id.value.toString}"

  private def convertToAggregate(value: Int): Record =
    AdvertiserId(value)

  private def convertToValue(aggregate: Record): Int = {
    aggregate.value
  }

}
