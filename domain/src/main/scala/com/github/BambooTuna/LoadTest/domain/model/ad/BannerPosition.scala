package com.github.BambooTuna.LoadTest.domain.model.ad

case class BannerPosition(value: Int) {
  require(value >= 0)
}
