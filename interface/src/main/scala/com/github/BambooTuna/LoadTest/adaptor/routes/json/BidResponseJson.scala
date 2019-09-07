package com.github.BambooTuna.LoadTest.adaptor.routes.json

case class BidResponseJson(
    id: String,
    bid_price: Double,
    advertiser_id: Int,
    nurl: String
)
