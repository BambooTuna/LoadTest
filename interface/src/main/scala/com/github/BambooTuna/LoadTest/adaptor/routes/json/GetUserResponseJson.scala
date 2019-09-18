package com.github.BambooTuna.LoadTest.adaptor.routes.json

import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.UserDeviceId

case class GetUserResponseJson(user: Option[UserDeviceId], error_messages: Seq[String] = Seq.empty) extends ResponseJson
