package com.github.BambooTuna.LoadTest.domain.model.dsp

import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.{ AdvertiserId, UserDeviceId }
import com.github.BambooTuna.LoadTest.domain.model.dsp.user.GameInstallCount

case class UserInfo(userId: UserDeviceId, advertiserId: AdvertiserId, gameInstallCount: GameInstallCount)
