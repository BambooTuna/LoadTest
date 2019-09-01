package com.github.BambooTuna.LoadTest.boot.server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.{ GET, POST, PUT }
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.server.Directives._
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc.JdbcSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.{ OnRedisClient, OnSlickClient }
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.RedisSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc.UserRepositoryOnJDBCImpl
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.UserRepositoryOnRedisImpl
import com.github.BambooTuna.LoadTest.usecase.{ AddUserUseCaseImpl, GetUserUseCaseImpl }

object Routes {

  val logger = LoggerFactory.getLogger(getClass)

  def createRouter(jdbcSetting: JdbcSetting, redisSetting: RedisSetting)(implicit system: ActorSystem,
                                                                         materializer: ActorMaterializer): Router = {

    val slickClient: OnSlickClient = jdbcSetting.client
    val redisClient: OnRedisClient = redisSetting.client

    commonRouter + mysqlRouter(slickClient) + redisRouter(redisClient)
  }

  def commonRouter(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET, "", CommonRoute().top),
      route(GET, "ping", CommonRoute().ping)
    )

  def mysqlRouter(client: OnSlickClient)(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET, "user" / "get", GetUserRoute(GetUserUseCaseImpl(new UserRepositoryOnJDBCImpl(client))).route),
      route(POST, "user" / "add", AddUserRoute(AddUserUseCaseImpl(new UserRepositoryOnJDBCImpl(client))).route),
      route(PUT, "user" / "update", EditUserRoute().route)
    )

  def redisRouter(client: OnRedisClient)(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET,
            "user" / "get",
            GetUserRoute(GetUserUseCaseImpl(new UserRepositoryOnRedisImpl(client, Some(10)))).route),
      route(POST,
            "user" / "add",
            AddUserRoute(AddUserUseCaseImpl(new UserRepositoryOnRedisImpl(client, Some(10)))).route),
      route(PUT, "user" / "update", EditUserRoute().route)
    )

}
