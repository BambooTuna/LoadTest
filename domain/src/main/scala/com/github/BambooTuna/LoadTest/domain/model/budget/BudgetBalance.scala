package com.github.BambooTuna.LoadTest.domain.model.budget

case class BudgetBalance(value: Double) {
  require(value >= 0)

  def add(value: Double): BudgetBalance = copy(this.value + value)

}
