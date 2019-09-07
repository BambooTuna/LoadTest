package com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis

import com.github.BambooTuna.LoadTest.domain.model.user._

trait UserComponentOnRedis {

  protected def generateKey(id: UserId): String = s"user_${id.value.toString}"

}
