package com.github.BambooTuna.LoadTest.domain.model.budget

case class BudgetDifferencePrice(value: Double) {

  def +(value: BudgetDifferencePrice): BudgetDifferencePrice = copy(this.value + value.value)

}
