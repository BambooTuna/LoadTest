package com.github.BambooTuna.LoadTest.adaptor.routes.json

case class AddUserInfoResponseJson(data: Seq[DeviceIdJson], error_messages: Seq[String] = Seq.empty)
    extends ResponseJson
