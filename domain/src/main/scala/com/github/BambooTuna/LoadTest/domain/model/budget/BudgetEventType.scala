package com.github.BambooTuna.LoadTest.domain.model.budget

object BudgetEventType {

  def withInt(value: Int): BudgetEventType = value match {
    case 0 => Absolute
    case 1 => Difference
  }

}
sealed trait BudgetEventType
case object Absolute   extends BudgetEventType
case object Difference extends BudgetEventType
