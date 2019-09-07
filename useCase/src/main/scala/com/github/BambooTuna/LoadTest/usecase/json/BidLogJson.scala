package com.github.BambooTuna.LoadTest.usecase.json

case class BidLogJson(
    id: String,
    timestamp: Long,
    device_id: String,
    banner_size: Int,
    media_id: Int,
    os_type: Int,
    banner_position: Int,
    is_interstitial: Int,
    floor_price: Double,
    click: Int
)
