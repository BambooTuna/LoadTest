package com.github.BambooTuna.LoadTest.adaptor.storage.dao.aerospike

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnAerospikeClient
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.AdvertiserId
import com.github.BambooTuna.LoadTest.domain.model.budget.{ BudgetDifferencePrice, BudgetEventModel, BudgetEventType }

trait BudgetComponentOnAerospike {

  val client: OnAerospikeClient

  protected def generateKey(id: AdvertiserId): String = s"budget_${id.value.toString}"

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
