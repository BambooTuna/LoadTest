package com.github.BambooTuna.LoadTest.adaptor.routes.json

case class BidRequestRequestJson(
    id: String,
    timestamp: Long,
    device_id: String,
    banner_size: Int,
    media_id: Int,
    os_type: Int,
    banner_position: Int,
    floor_price: Double,
)
