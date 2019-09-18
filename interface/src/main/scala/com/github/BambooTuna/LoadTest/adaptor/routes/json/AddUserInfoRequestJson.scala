package com.github.BambooTuna.LoadTest.adaptor.routes.json

import com.github.BambooTuna.LoadTest.usecase.json.UserInfoJson

case class AddUserInfoRequestJson(data: Seq[UserInfoJson]) {
  require(data.size <= 1000)
}
