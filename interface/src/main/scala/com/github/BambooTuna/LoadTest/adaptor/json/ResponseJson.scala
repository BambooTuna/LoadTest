package com.github.BambooTuna.LoadTest.adaptor.json

trait ResponseJson {
  val error_messages: Seq[String]
  def isSuccessful: Boolean = error_messages.isEmpty
}
