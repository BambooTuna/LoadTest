package com.github.BambooTuna.LoadTest.boot.server

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnRedisClient

case class ClientCluster(
    redisClients: Seq[OnRedisClient]
) {
  //TODO
  require(redisClients.size == 7)
}
