package com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile

import redis.RedisClient

trait OnRedisClient extends Client {

  override type Connection = RedisClient

}
