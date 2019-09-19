package com.github.BambooTuna.LoadTest.domain.model.dsp.ad

import com.github.BambooTuna.LoadTest.domain.aggregate.AggregateIntId

case class MediaId(value: Int) extends AggregateIntId {
  require(value >= 0)
}