package com.github.BambooTuna.LoadTest.adaptor.storage.repository

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.RepositorySupport
import com.github.BambooTuna.LoadTest.domain.model.ad.{ AdRequestId, AdvertiserId }
import monix.eval.Task

trait AdIdRepository extends RepositorySupport[Task, AdRequestId, (AdRequestId, AdvertiserId)]
