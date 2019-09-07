package com.github.BambooTuna.LoadTest.domain.model.ad

case class FloorPrice(value: Double) {
  require(value >= 0)
}
