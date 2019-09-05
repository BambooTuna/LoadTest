package com.github.BambooTuna.LoadTest.domain.model.dsp.user

import com.github.BambooTuna.LoadTest.domain.aggregate.AggregateLongId

case class GameInstallCount(value: Long) extends AggregateLongId
