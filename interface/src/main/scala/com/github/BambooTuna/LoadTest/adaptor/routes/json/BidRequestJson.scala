package com.github.BambooTuna.LoadTest.adaptor.routes.json

import com.github.BambooTuna.LoadTest.domain.model.ad.AdRequestExt

case class BidRequestJson(
    id: String,
    timestamp: Long,
    device_id: String,
    banner_size: Int,
    media_id: Int,
    os_type: Int,
    banner_position: Int,
    is_interstitial: Int,
    floor_price: Double,
    ext: AdRequestExt
)
