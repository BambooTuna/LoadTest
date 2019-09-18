package com.github.BambooTuna.LoadTest.boot.server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.{ GET, POST }
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.server.Directives._
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.aerospike.AerospikeSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc.JdbcSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.{ OnAerospikeClient, OnRedisClient, OnSlickClient }
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.RedisSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.{
  AdvertiserIdRepositoryOnRedisImpl,
  BudgetRepositoryOnRedisImpl,
  UserInfoRepositoryOnRedisImpl
}
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.WinNoticeEndpoint
import com.github.BambooTuna.LoadTest.usecase.{ GetAdvertiserIdUseCase, _ }

object Routes {

  val logger = LoggerFactory.getLogger(getClass)

  def createRouter(jdbcSetting: JdbcSetting, redisSettings: RedisSetting, aerospikeSetting: AerospikeSetting)(
      implicit system: ActorSystem,
      materializer: ActorMaterializer
  ): Router = {

    val mysqlClient: OnSlickClient         = jdbcSetting.client
    val redisClient: OnRedisClient         = redisSettings.client
    val aerospikeClient: OnAerospikeClient = aerospikeSetting.client

    commonRouter +
    bidRequestRouter(mysqlClient, redisClient, aerospikeClient) +
    winNoticeRouter(mysqlClient, redisClient, aerospikeClient)
  }

  def commonRouter(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET, "", CommonRoute().top),
      route(GET, "ping", CommonRoute().ping)
    )

  def bidRequestRouter(mysqlClient: OnSlickClient, redisClient: OnRedisClient, aerospikeClient: OnAerospikeClient)(
      implicit system: ActorSystem,
      materializer: ActorMaterializer
  ): Router = {

    val userInfoRepository = new UserInfoRepositoryOnRedisImpl(redisClient)
    val budgetRepository   = new BudgetRepositoryOnRedisImpl(redisClient)

    val getUserInfoUseCase = GetUserInfoUseCase(
      UserInfoRepositoryBalancer(Seq(userInfoRepository))
    )

    val getBudgetUseCase = GetBudgetUseCase(
      BudgetRepositoryBalancer(Seq(budgetRepository))
    )

    val getModelUseCase = GetModelUseCase()

    Router(
      route(
        POST,
        "bid_request",
        BidRequestRoute(
          BidRequestUseCase(
            getUserInfoUseCase,
            getBudgetUseCase,
            getModelUseCase,
            WinNoticeEndpoint("http://34.84.137.136:8080/win") //TODO
          )
        ).route
      )
    )
  }

  def winNoticeRouter(mysqlClient: OnSlickClient, redisClient: OnRedisClient, aerospikeClient: OnAerospikeClient)(
      implicit system: ActorSystem,
      materializer: ActorMaterializer
  ): Router = {

    val userInfoRepository     = new UserInfoRepositoryOnRedisImpl(redisClient)
    val budgetRepository       = new BudgetRepositoryOnRedisImpl(redisClient)
    val advertiserIdRepository = new AdvertiserIdRepositoryOnRedisImpl(redisClient)

    val getAdvertiserIdUseCase = GetAdvertiserIdUseCase(
      AdvertiserIdRepositoryBalancer(Seq(advertiserIdRepository))
    )

    val reduceBudgetFromWinNoticeUseCase = ReduceBudgetFromWinNoticeUseCase(
      BudgetRepositoryBalancer(Seq(budgetRepository))
    )(getAdvertiserIdUseCase)

    val addUserInfoUseCase = AddUserInfoUseCase(
      UserInfoRepositoryBalancer(Seq(userInfoRepository))
    )

    val setBudgetUseCase: SetBudgetUseCase = SetBudgetUseCase(
      BudgetRepositoryBalancer(Seq(budgetRepository))
    )

    Router(
      route(POST, "win", ReduceBudgetFromWinNoticeRoute(reduceBudgetFromWinNoticeUseCase).route),
      route(POST, "user" / "add", AddUserInfoRoute(addUserInfoUseCase).route),
      route(POST, "budget" / "set", SetBudgetRoute(setBudgetUseCase).route)
    )
  }

}
