package com.github.BambooTuna.LoadTest.domain.model.ad

case class OsType(value: Int) {
  require(value >= 0)
}
