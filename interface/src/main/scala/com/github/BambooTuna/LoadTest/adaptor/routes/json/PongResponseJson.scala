package com.github.BambooTuna.LoadTest.adaptor.routes.json

case class PongResponseJson(
    message: String,
    error_messages: Seq[String] = Seq.empty
) extends ResponseJson
