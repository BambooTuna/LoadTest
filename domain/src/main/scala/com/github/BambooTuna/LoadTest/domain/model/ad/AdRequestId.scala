package com.github.BambooTuna.LoadTest.domain.model.ad

case class AdRequestId(value: String) {
  require(value.nonEmpty)
}
