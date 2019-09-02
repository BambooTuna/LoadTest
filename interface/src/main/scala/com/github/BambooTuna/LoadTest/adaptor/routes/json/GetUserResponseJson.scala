package com.github.BambooTuna.LoadTest.adaptor.routes.json

case class GetUserResponseJson(user: Option[UserJson], error_messages: Seq[String] = Seq.empty) extends ResponseJson
