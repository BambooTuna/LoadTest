package com.github.BambooTuna.LoadTest.domain.model.dsp.ad

import com.github.BambooTuna.LoadTest.domain.aggregate.AggregateStringId

case class WinNoticeEndpoint(value: String) extends AggregateStringId {
  require(value.contains("http") && value.nonEmpty)
}
