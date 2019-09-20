package com.github.BambooTuna.LoadTest.adaptor.routes.json

import com.github.BambooTuna.LoadTest.usecase.json.UserInfoJson

case class GetUserInfoResponseJson(data: Option[UserInfoJson] = None, error_messages: Seq[String] = Seq.empty)
    extends ResponseJson
