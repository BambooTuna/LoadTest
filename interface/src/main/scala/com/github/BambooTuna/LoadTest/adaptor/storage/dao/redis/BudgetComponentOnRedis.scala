package com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis

import com.github.BambooTuna.LoadTest.domain.model.ad.AdvertiserId
import com.github.BambooTuna.LoadTest.domain.model.budget._

trait BudgetComponentOnRedis {

  protected def generateAbsKey(id: AdvertiserId): String = s"abs_${id.value.toString}"
  protected def generateKey(id: AdvertiserId): String    = s"b_${id.value.toString}"

  protected def chooseKey(budgetEventType: BudgetEventType): AdvertiserId => String =
    budgetEventType match {
      case Absolute   => generateAbsKey
      case Difference => generateKey
    }

  case class BudgetEventModelJson(budget_type: Int, budget_balance: Double)

  def convertToJson(item: BudgetEventModel): BudgetEventModelJson =
    BudgetEventModelJson(
      item.budgetEventType.value,
      item.budgetDifferencePrice.value
    )

  def convertToRecord(item: BudgetEventModelJson): BudgetEventModel =
    BudgetEventModel(
      BudgetEventType.withInt(item.budget_type),
      BudgetDifferencePrice(item.budget_balance)
    )

}
