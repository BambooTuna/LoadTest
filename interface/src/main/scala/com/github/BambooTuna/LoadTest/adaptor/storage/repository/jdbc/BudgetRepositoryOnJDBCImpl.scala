package com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc._
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnSlickClient
import com.github.BambooTuna.LoadTest.domain.model.budget.{ BudgetDifferencePrice, BudgetEventModel, BudgetEventType }
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.AdvertiserId
import com.github.BambooTuna.LoadTest.domain.setting.TimeZoneSetting
import monix.eval.Task

class BudgetRepositoryOnJDBCImpl(val client: OnSlickClient) extends BudgetRepositoryOnJDBC with DaoOnSlick {

  import client.profile.api._

  val tableName = "budget"

  case class BudgetRecord(
      advertiser_id: Int,
      budget_type: Int,
      budget_balance: Double,
      createdAt: java.time.ZonedDateTime
  )

  case class Budgets(tag: Tag) extends Table[BudgetRecord](tag, tableName) {
    def advertiser_id  = column[Int]("advertiser_id")
    def budget_type    = column[Int]("budget_type")
    def budget_balance = column[Double]("budget_balance")
    def createAt       = column[java.time.ZonedDateTime]("create_at")
    def * =
      (
        advertiser_id,
        budget_type,
        budget_balance,
        createAt
      ) <> (BudgetRecord.tupled, BudgetRecord.unapply)
  }

  object BudgetDao extends TableQuery(Budgets)
  val dao: BudgetDao.type = BudgetDao

  private def convertToRecord(id: AdvertiserId, item: BudgetEventModel): BudgetRecord =
    BudgetRecord(
      id.value,
      item.budgetEventType.value,
      item.budgetDifferencePrice.value,
      java.time.Instant.now().atZone(TimeZoneSetting.zone)
    )

  private def convertToAggregate(item: BudgetRecord): BudgetEventModel =
    BudgetEventModel(
      BudgetEventType.withInt(item.budget_type),
      BudgetDifferencePrice(item.budget_balance)
    )

  def calculateBudgetBalance(models: Seq[BudgetEventModel]): BudgetEventModel =
    models.reduce(_ + _)

  private val resolveByIdCompiled = Compiled(
    (id: Rep[Int]) => dao.filter(_.advertiser_id === id).sortBy(_.createAt.asc)
  )
  def resolveById(id: Id): Task[Option[Record]] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db.run(resolveByIdCompiled(id.value).result)
      }.map(_.map(convertToAggregate)).map(l => Some(calculateBudgetBalance(l)))
  }

  val insertCompiled = Compiled(dao.filter(_ => true: Rep[Boolean]))
  override def insert(id: Id, record: Record): Task[Long] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db.run(insertCompiled += convertToRecord(id, record))
      }.map(_.toLong)
  }

}
