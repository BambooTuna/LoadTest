package com.github.BambooTuna.LoadTest.domain.model.user

case class Age(value: Int) {
  require(value >= 0)
}
