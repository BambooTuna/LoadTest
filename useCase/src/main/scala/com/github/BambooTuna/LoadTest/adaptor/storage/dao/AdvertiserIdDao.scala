package com.github.BambooTuna.LoadTest.adaptor.storage.dao

import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.{AdvertiserId, BidRequestId}
import monix.eval.Task

trait AdvertiserIdDao extends RepositorySupport[Task, BidRequestId, AdvertiserId]