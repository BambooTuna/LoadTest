package com.github.BambooTuna.LoadTest.adaptor.storage.repository

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.RepositorySupport
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.{ BidRequestId, AdvertiserId }
import monix.eval.Task

trait AdvertiserIdRepository extends RepositorySupport[Task, BidRequestId, (BidRequestId, AdvertiserId)]
