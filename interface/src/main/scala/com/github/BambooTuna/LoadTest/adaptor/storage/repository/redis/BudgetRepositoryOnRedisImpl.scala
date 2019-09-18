package com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnRedisClient
import com.github.BambooTuna.LoadTest.domain.model.budget._
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.AdvertiserId
import monix.eval.Task

import scala.concurrent.ExecutionContext

class BudgetRepositoryOnRedisImpl(client: OnRedisClient) extends BudgetRepositoryOnRedis {

  import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.IntByteStringSerializer._
  import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.IntByteStringDeserializer._

  implicit val executionContext: ExecutionContext = client.db.executionContext

  override def resolveById(id: Id): Task[Option[Record]] = {
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
    ???
  }

  override def insert(id: Id, record: Record): Task[Long] = {
    record.budgetEventType match {
      case Absolute =>
        Task
          .deferFutureAction { implicit ec =>
            client.db
              .set[Int](chooseKey(Absolute)(id), record.budgetDifferencePrice.value.toInt, exSeconds = None)
              .map(r => if (r) 1L else 0L)
          }
      case Difference =>
        Task
          .deferFutureAction { implicit ec =>
            client.db
              .incr(chooseKey(Difference)(id))
          }
    }
  }

  private def generateAbsKey(id: AdvertiserId): String = s"abs_${id.value.toString}"
  private def generateKey(id: AdvertiserId): String    = s"b_${id.value.toString}"
  private def chooseKey(budgetEventType: BudgetEventType): AdvertiserId => String =
    budgetEventType match {
      case Absolute   => generateAbsKey
      case Difference => generateKey
    }

  private def convertToJson(item: BudgetEventModel): BudgetEventModelJson =
    BudgetEventModelJson(
      item.budgetEventType.value,
      item.budgetDifferencePrice.value
    )

  private def convertToAggregate(item: BudgetEventModelJson): BudgetEventModel =
    BudgetEventModel(
      BudgetEventType.withInt(item.budget_type),
      BudgetDifferencePrice(item.budget_balance)
    )

  case class BudgetEventModelJson(budget_type: Int, budget_balance: Double)

}
