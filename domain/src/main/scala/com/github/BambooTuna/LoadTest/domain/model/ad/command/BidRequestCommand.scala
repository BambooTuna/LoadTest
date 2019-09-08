package com.github.BambooTuna.LoadTest.domain.model.ad.command

import com.github.BambooTuna.LoadTest.domain.model.ad._

case class BidRequestCommand(
    id: AdRequestId,
    timestamp: AdRequestTimestamp,
    deviceId: DeviceId,
    bannerSize: BannerSize,
    mediaId: MediaId,
    osType: OsType,
    bannerPosition: BannerPosition,
    isInterstitial: IsInterstitial,
    floorPrice: FloorPrice,
    ext: AdRequestExt
)
