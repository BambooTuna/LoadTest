package com.github.BambooTuna.LoadTest.domain.model.budget

object BudgetEventType {

  def withInt(value: Int): BudgetEventType = value match {
    case 0 => Absolute
    case 1 => Difference
  }

}
sealed case class BudgetEventType(value: Int)
object Absolute   extends BudgetEventType(0)
object Difference extends BudgetEventType(1)
