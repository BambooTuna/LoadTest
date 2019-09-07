package com.github.BambooTuna.LoadTest.adaptor.storage.repository

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.RepositorySupport
import com.github.BambooTuna.LoadTest.domain.model.ad.AdvertiserId
import com.github.BambooTuna.LoadTest.domain.model.budget._
import monix.eval.Task

trait BudgetRepository extends RepositorySupport[Task, AdvertiserId, (AdvertiserId, BudgetEventModel)]
