package com.github.BambooTuna.LoadTest.domain.model.ad.command

import com.github.BambooTuna.LoadTest.domain.model.ad.{ AdRequestId, AdvertiserId, BidPrice, WinRedirectUrl }

case class BidResponseCommand(
    id: AdRequestId,
    bidPrice: BidPrice,
    advertiserId: AdvertiserId,
    winUrl: WinRedirectUrl
)
