package com.github.BambooTuna.LoadTest.adaptor.storage.repository

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.RepositorySupport
import com.github.BambooTuna.LoadTest.domain.model.user.UserId
import com.github.BambooTuna.LoadTest.usecase.json.UserDataJson
import monix.eval.Task

trait UserRepository extends RepositorySupport[Task, UserId, (UserId, UserDataJson)]
