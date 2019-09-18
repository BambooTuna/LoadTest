package com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc

import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.AdvertiserId
import com.github.BambooTuna.LoadTest.domain.model.budget.{ BudgetDifferencePrice, BudgetEventModel, BudgetEventType }
import com.github.BambooTuna.LoadTest.domain.setting.TimeZoneSetting
import slick.jdbc.MySQLProfile.api._

trait BudgetComponentOnJDBC extends DaoOnSlick {

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

  def convertToRecord(id: AdvertiserId, item: BudgetEventModel): BudgetRecord =
    BudgetRecord(
      id.value,
      item.budgetEventType.value,
      item.budgetDifferencePrice.value,
      java.time.Instant.now().atZone(TimeZoneSetting.zone)
    )

  def convertToAggregate(item: BudgetRecord): BudgetEventModel =
    BudgetEventModel(
      BudgetEventType.withInt(item.budget_type),
      BudgetDifferencePrice(item.budget_balance)
    )

}
