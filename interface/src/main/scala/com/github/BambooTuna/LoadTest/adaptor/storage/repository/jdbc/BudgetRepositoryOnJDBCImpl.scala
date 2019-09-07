package com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc._
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnSlickClient
import com.github.BambooTuna.LoadTest.domain.model.budget.{ BudgetDifferencePrice, BudgetEventModel, BudgetEventType }
import monix.eval.Task

class BudgetRepositoryOnJDBCImpl(val client: OnSlickClient) extends BudgetRepositoryOnJDBC with BudgetComponentOnJDBC {

  import client.profile.api._

  val dao: BudgetDao.type = BudgetDao

  override def get(id: Id): Task[Option[Record]] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db.run(dao.filter(_.advertiser_id === id.value).result)
      }.map(_.headOption.map(convertToAggregate).map((id, _)))
  }

  override def resolveById(id: Id): Task[Seq[Record]] = {
    //TODO
//    Task
//      .deferFutureAction { implicit ec =>
//        client.db.run(dao.filter(_.advertiser_id === id.value).sortBy(_.createAt.asc).result)
//      }.map(_.map(convertToAggregate).map((id, _)))

    Task {
      Seq(
        (
          id,
          BudgetEventModel(
            BudgetEventType.withInt(0),
            BudgetDifferencePrice(1000)
          )
        )
      )
    }

  }

  override def getMulti(ids: Seq[Id]): Task[Seq[Record]] = ???

  override def put(record: Record): Task[Long] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db.run(dao += convertToRecord(record._1, record._2))
      }.map(_.toLong)
  }

  override def putMulti(records: Seq[Record]): Task[Long] = ???

  override def delete(id: Id): Task[Long] = ???

  override def deleteMulti(ids: Seq[Id]): Task[Long] = ???

  override def softDelete(id: Id): Task[Long] = ???

  override def softDeleteMulti(ids: Seq[Id]): Task[Long] = ???

}
