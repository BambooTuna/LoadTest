package com.github.BambooTuna.LoadTest.domain.model.dsp.ad

import com.github.BambooTuna.LoadTest.domain.aggregate.AggregateDoubleId

case class BidPrice(value: Double) extends AggregateDoubleId {
  require(value >= 0)
}
