package com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnRedisClient
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.UserComponentOnRedis
import monix.eval.Task

import scala.concurrent.ExecutionContext

class UserRepositoryOnRedisImpl(client: OnRedisClient, expireSeconds: Option[Long])
    extends UserRepositoryOnRedis
    with UserComponentOnRedis {

  import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.JsonCodecToByteStringSerdesConversion._
  import io.circe.generic.auto._

  implicit val executionContext: ExecutionContext = client.db.executionContext

  override def get(id: Id): Task[Option[Record]] =
    Task
      .deferFutureAction { implicit ec =>
        client.db.get[RecordJson](generateKey(id)).map(_.map(convertToData))
      }
  override def getMulti(ids: Seq[Id]): Task[Seq[Record]] = ???

  override def put(record: Record): Task[Long] = {
    val json = convertToJson(record)
    Task
      .deferFutureAction { implicit ec =>
        client.db
          .set[RecordJson](generateKey(record.userId), json, expireSeconds)
          .map(_ => 1L)
      }
  }
  override def putMulti(records: Seq[Record]): Task[Long] = ???

  override def delete(id: Id): Task[Long]            = ???
  override def deleteMulti(ids: Seq[Id]): Task[Long] = ???

  override def softDelete(id: Id): Task[Long]            = ???
  override def softDeleteMulti(ids: Seq[Id]): Task[Long] = ???

}
