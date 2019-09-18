package com.github.BambooTuna.LoadTest.adaptor.storage.dao

import com.github.BambooTuna.LoadTest.domain.model.budget.BudgetEventModel
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.AdvertiserId
import monix.eval.Task

trait BudgetDao extends RepositorySupport[Task, AdvertiserId, BudgetEventModel]
