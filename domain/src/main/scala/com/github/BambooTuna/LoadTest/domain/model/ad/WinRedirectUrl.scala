package com.github.BambooTuna.LoadTest.domain.model.ad

case class WinRedirectUrl(value: String) {
  require(value.contains("http") && value.nonEmpty)
}
