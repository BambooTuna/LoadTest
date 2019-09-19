package com.github.BambooTuna.LoadTest.domain.model.budget

object BudgetEventType {

  def withInt(value: Int): BudgetEventType = value match {
    case 0 => Absolute
    case 1 => Difference
  }

}
sealed trait BudgetEventType {
  val value: Int
}
case object Absolute extends BudgetEventType {
  override val value: Int = 0
}
case object Difference extends BudgetEventType {
  override val value: Int = 1
}
