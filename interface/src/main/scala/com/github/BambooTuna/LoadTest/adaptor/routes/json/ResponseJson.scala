package com.github.BambooTuna.LoadTest.adaptor.routes.json

trait ResponseJson {
  val error_messages: Seq[String]
  def isSuccessful: Boolean = error_messages.isEmpty
}
