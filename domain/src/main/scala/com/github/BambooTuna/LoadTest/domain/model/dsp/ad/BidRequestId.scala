package com.github.BambooTuna.LoadTest.domain.model.dsp.ad

import com.github.BambooTuna.LoadTest.domain.aggregate.AggregateStringId

case class BidRequestId(value: String) extends AggregateStringId {
  require(value.nonEmpty)
}
