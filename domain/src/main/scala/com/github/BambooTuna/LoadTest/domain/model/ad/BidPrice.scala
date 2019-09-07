package com.github.BambooTuna.LoadTest.domain.model.ad

import com.github.BambooTuna.LoadTest.domain.model.budget.BudgetBalance

case class BidPrice(value: Double, budgetPrice: BudgetBalance) {
  require(value <= budgetPrice.value)
}
