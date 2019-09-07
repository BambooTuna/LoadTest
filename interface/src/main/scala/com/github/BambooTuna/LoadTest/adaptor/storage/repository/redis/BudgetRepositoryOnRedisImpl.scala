package com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnRedisClient
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.BudgetComponentOnRedis
import com.github.BambooTuna.LoadTest.domain.model.budget._
import monix.eval.Task

import scala.concurrent.ExecutionContext

class BudgetRepositoryOnRedisImpl(client: OnRedisClient) extends BudgetRepositoryOnRedis with BudgetComponentOnRedis {

  import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.IntByteStringSerializer._
  import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.IntByteStringDeserializer._

  implicit val executionContext: ExecutionContext = client.db.executionContext

  override def get(id: Id): Task[Option[Record]] = ???

  override def resolveById(id: Id): Task[BudgetBalance] = {
    Task.parMap2(
      Task
        .deferFutureAction { implicit ec =>
          client.db.get[Int](generateAbsKey(id)).map(_.get)
        },
      Task
        .deferFutureAction { implicit ec =>
          client.db.get[Int](generateKey(id)).map(_.get)
        }
    )((abs, dif) => {
      //TODO
      BudgetBalance(abs - dif * 120)
    })
  }

  override def getMulti(ids: Seq[Id]): Task[Seq[Record]] = ???

  override def put(record: Record): Task[Long] = {
    record._2.budgetEventType match {
      case Absolute =>
        Task
          .deferFutureAction { implicit ec =>
            client.db
              .set[Int](chooseKey(Absolute)(record._1), record._2.budgetDifferencePrice.value.toInt, exSeconds = None)
              .map(r => if (r) 1L else 0L)
          }
      case Difference =>
        Task
          .deferFutureAction { implicit ec =>
            client.db
              .incr(chooseKey(Difference)(record._1))
          }
    }

  }
  override def putMulti(records: Seq[Record]): Task[Long] = ???

  override def delete(id: Id): Task[Long]            = ???
  override def deleteMulti(ids: Seq[Id]): Task[Long] = ???

  override def softDelete(id: Id): Task[Long]            = ???
  override def softDeleteMulti(ids: Seq[Id]): Task[Long] = ???

}
