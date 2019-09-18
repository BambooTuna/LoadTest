package com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnRedisClient
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.AdIdComponentOnRedis
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.AdvertiserId
import monix.eval.Task

import scala.concurrent.ExecutionContext

class AdvertiserIdRepositoryOnRedisImpl(client: OnRedisClient) extends AdvertiserIdRepositoryOnRedis with AdIdComponentOnRedis {

  import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.IntByteStringSerializer._
  import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.IntByteStringDeserializer._

  implicit val executionContext: ExecutionContext = client.db.executionContext

  override def get(id: Id): Task[Option[Record]] =
    Task
      .deferFutureAction { implicit ec =>
        client.db.get[Int](id.value).map(_.map(v => (id, AdvertiserId(v))))
      }

  override def getMulti(ids: Seq[Id]): Task[Seq[Record]] = ???

  override def put(record: Record): Task[Long] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db
          .set[Int](record._1.value, record._2.value, exSeconds = None)
          .map(r => if (r) 1L else 0L)
      }
  }
  override def putMulti(records: Seq[Record]): Task[Long] = ???

  override def delete(id: Id): Task[Long]            = ???
  override def deleteMulti(ids: Seq[Id]): Task[Long] = ???

  override def softDelete(id: Id): Task[Long]            = ???
  override def softDeleteMulti(ids: Seq[Id]): Task[Long] = ???

}
