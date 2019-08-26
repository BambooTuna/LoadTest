package com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.RepositorySupport
import com.github.BambooTuna.LoadTest.domain.model.user.{ User, UserId }
import monix.eval.Task

trait UserRepositoryOnJDBC extends RepositorySupport[Task, UserId, User]
