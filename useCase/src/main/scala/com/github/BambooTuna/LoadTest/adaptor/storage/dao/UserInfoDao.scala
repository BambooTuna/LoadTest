package com.github.BambooTuna.LoadTest.adaptor.storage.dao

import com.github.BambooTuna.LoadTest.domain.model.dsp.UserInfo
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.UserDeviceId
import monix.eval.Task

trait UserInfoDao extends RepositorySupport[Task, UserDeviceId, UserInfo]
