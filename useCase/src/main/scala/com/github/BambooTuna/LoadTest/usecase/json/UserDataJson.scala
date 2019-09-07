package com.github.BambooTuna.LoadTest.usecase.json

case class UserDataJson(
    device_id: String,
    advertiser_id: Int,
    game_install_count: Int,
    game_login_count: Int,
    game_paid_count: Int,
    game_tutorial_count: Int,
    game_extension_count: Int
)
