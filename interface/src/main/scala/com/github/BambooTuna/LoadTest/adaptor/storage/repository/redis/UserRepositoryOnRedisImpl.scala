package com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnRedisClient
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.UserComponentOnRedis
import com.github.BambooTuna.LoadTest.usecase.json.UserDataJson
import monix.eval.Task

import scala.concurrent.ExecutionContext

class UserRepositoryOnRedisImpl(client: OnRedisClient) extends UserRepositoryOnRedis with UserComponentOnRedis {

  import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.JsonCodecToByteStringSerdesConversion._
  import io.circe.generic.auto._

  implicit val executionContext: ExecutionContext = client.db.executionContext

  override def get(id: Id): Task[Option[Record]] =
    Task
      .deferFutureAction { implicit ec =>
        client.db.get[UserDataJson](generateKey(id)).map(_.map((id, _)))
      }
  override def getMulti(ids: Seq[Id]): Task[Seq[Record]] = ???

  override def put(record: Record): Task[Long] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db
          .set[UserDataJson](generateKey(record._1), record._2, exSeconds = None)
          .map(r => if (r) 1L else 0L)
      }
  }
  override def putMulti(records: Seq[Record]): Task[Long] = ???

  override def delete(id: Id): Task[Long]            = ???
  override def deleteMulti(ids: Seq[Id]): Task[Long] = ???

  override def softDelete(id: Id): Task[Long]            = ???
  override def softDeleteMulti(ids: Seq[Id]): Task[Long] = ???

}
