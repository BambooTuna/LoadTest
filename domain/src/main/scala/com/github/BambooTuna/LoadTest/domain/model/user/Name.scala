package com.github.BambooTuna.LoadTest.domain.model.user

case class Name(value: String) {
  require(value.length <= 100)
}
