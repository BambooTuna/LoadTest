package com.github.BambooTuna.LoadTest.domain.model.ad

case class AdvertiserId(value: Int) {
  require(value >= 0 && value <= 20)
}
