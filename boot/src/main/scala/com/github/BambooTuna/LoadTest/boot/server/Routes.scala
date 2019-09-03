package com.github.BambooTuna.LoadTest.boot.server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.{GET, POST, PUT}
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.server.Directives._
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.aerospike.AerospikeSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc.JdbcSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.{OnAerospikeClient, OnRedisClient, OnSlickClient}
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.RedisSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.aerospike.UserRepositoryOnAerospikeImpl
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc.UserRepositoryOnJDBCImpl
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.UserRepositoryOnRedisImpl
import com.github.BambooTuna.LoadTest.usecase.{AddUserUseCaseImpl, GetUserUseCaseImpl}

object Routes {

  val logger = LoggerFactory.getLogger(getClass)

  def createRouter(jdbcSetting: JdbcSetting, redisSetting: RedisSetting, aerospikeSetting: AerospikeSetting)(implicit system: ActorSystem,
                                                                         materializer: ActorMaterializer): Router = {

    val slickClient: OnSlickClient = jdbcSetting.client
    val redisClient: OnRedisClient = redisSetting.client
    val aerospikeClient: OnAerospikeClient = aerospikeSetting.client

    commonRouter + mysqlRouter(slickClient) + redisRouter(redisClient) + aerospikeRouter(aerospikeClient)
  }

  def commonRouter(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET, "", CommonRoute().top),
      route(GET, "ping", CommonRoute().ping)
    )

  def mysqlRouter(client: OnSlickClient)(implicit materializer: ActorMaterializer): Router = {
    val repository = new UserRepositoryOnJDBCImpl(client)
    Router(
      route(GET, "user" / "get", GetUserRoute(GetUserUseCaseImpl(repository)).route),
      route(POST, "user" / "add", AddUserRoute(AddUserUseCaseImpl(repository)).route),
      route(PUT, "user" / "update", EditUserRoute().route)
    )
  }

  def redisRouter(client: OnRedisClient)(implicit materializer: ActorMaterializer): Router = {
    val repository = new UserRepositoryOnRedisImpl(client)
    Router(
      route(GET, "redis" / "user" / "get", GetUserRoute(GetUserUseCaseImpl(repository)).route),
      route(POST, "redis" / "user" / "add", AddUserRoute(AddUserUseCaseImpl(repository)).route),
      route(PUT, "redis" / "user" / "update", EditUserRoute().route)
    )
  }

  def aerospikeRouter(client: OnAerospikeClient)(implicit materializer: ActorMaterializer): Router = {
    val repository = new UserRepositoryOnAerospikeImpl(client)
    Router(
      route(GET, "aerospike" / "user" / "get", GetUserRoute(GetUserUseCaseImpl(repository)).route),
      route(POST, "aerospike" / "user" / "add", AddUserRoute(AddUserUseCaseImpl(repository)).route),
      route(PUT, "aerospike" / "user" / "update", EditUserRoute().route)
    )
  }

}
