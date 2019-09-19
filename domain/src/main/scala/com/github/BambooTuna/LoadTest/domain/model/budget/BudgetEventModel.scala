package com.github.BambooTuna.LoadTest.domain.model.budget

case class BudgetEventModel(
    budgetEventType: BudgetEventType,
    budgetDifferencePrice: BudgetDifferencePrice
) {

  def +(budgetEventModel: BudgetEventModel): BudgetEventModel =
    this.budgetEventType match {
      case Absolute =>
        this.copy(
          budgetDifferencePrice = this.budgetDifferencePrice + budgetEventModel.budgetDifferencePrice
        )
      case Difference => budgetEventModel
    }

  def result: BudgetBalance =
    this.budgetEventType match {
      case Absolute =>
        BudgetBalance(this.budgetDifferencePrice.value)
      case Difference => throw new Exception("is not Absolute")
    }

}
