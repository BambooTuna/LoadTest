package com.github.BambooTuna.LoadTest.adaptor.storage.repository

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.RepositorySupport
import com.github.BambooTuna.LoadTest.domain.model.dsp.UserInfo
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.UserDeviceId
import monix.eval.Task

trait UserInfoRepository extends RepositorySupport[Task, UserDeviceId, (UserDeviceId, UserInfo)]
