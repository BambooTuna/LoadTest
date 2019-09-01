package com.github.BambooTuna.LoadTest.adaptor.storage.repository

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.RepositorySupport
import com.github.BambooTuna.LoadTest.domain.model.user.{ User, UserId }
import monix.eval.Task

trait UserRepository extends RepositorySupport[Task, UserId, User]
