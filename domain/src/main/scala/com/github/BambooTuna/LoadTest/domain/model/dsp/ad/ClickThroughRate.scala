package com.github.BambooTuna.LoadTest.domain.model.dsp.ad

import com.github.BambooTuna.LoadTest.domain.aggregate.AggregateDoubleId

case class ClickThroughRate(value: Double) extends AggregateDoubleId {
  require(0 <= value && value <= 1)
}
