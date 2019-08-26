package com.github.BambooTuna.LoadTest.adaptor.routes.json

case class AddUserResponseJson(id: Option[UserIdJson], error_messages: Seq[String] = Seq.empty) extends ResponseJson
