package com.github.BambooTuna.LoadTest.adaptor.routes.json

case class AddUserResponseJson(id: Long, error_messages: Seq[String] = Seq.empty) extends ResponseJson
