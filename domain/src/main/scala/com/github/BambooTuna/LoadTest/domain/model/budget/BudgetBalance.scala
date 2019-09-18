package com.github.BambooTuna.LoadTest.domain.model.budget

import com.github.BambooTuna.LoadTest.domain.aggregate.AggregateDoubleId

case class BudgetBalance(value: Double) extends AggregateDoubleId {
  require(value >= 0)

  def add(value: Double): BudgetBalance = copy(this.value + value)
}
