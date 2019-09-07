package com.github.BambooTuna.LoadTest.usecase.json

case class UserDataJson(
    device_id: String,
    advertiser_id: Int,
    game_install_count: Double,
    game_login_count: Double,
    game_paid_count: Double,
    game_tutorial_count: Double,
    game_extension_count: Double
) {
  require(1 <= advertiser_id && advertiser_id <= 20)
}
