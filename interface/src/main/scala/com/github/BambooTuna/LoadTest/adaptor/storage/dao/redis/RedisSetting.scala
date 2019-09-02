package com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis

import akka.actor.ActorSystem
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnRedisClient
import redis.RedisClient
import scala.concurrent.duration.FiniteDuration

case class RedisSetting(host: String,
                        port: Int,
                        password: Option[String] = None,
                        redis_db: Option[Int] = None,
                        connectTimeout: Option[FiniteDuration] = None) {

  def client(implicit system: ActorSystem) = new OnRedisClient {
    override val db = RedisClient(host, port, password, redis_db, connectTimeout = connectTimeout)
  }

}
