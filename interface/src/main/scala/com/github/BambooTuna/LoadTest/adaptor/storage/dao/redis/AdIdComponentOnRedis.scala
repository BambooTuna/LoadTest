package com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis

import com.github.BambooTuna.LoadTest.domain.model.user._

trait AdIdComponentOnRedis {

  protected def generateKey(id: UserId): String = s"adid_${id.value.toString}"

}
