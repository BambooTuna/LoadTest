package com.github.BambooTuna.LoadTest.domain.model.ad

case class MediaId(value: Int) {
  require(value >= 0)
}
