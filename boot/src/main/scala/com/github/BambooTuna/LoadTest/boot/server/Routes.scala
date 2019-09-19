package com.github.BambooTuna.LoadTest.boot.server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.{GET, POST}
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.server.Directives._
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.{AdvertiserIdDao, BudgetDao, UserInfoDao}
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.aerospike.AerospikeSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc.JdbcSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.{OnAerospikeClient, OnRedisClient, OnSlickClient}
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.RedisSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc.BudgetRepositoryOnJDBCImpl
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.{AdvertiserIdRepositoryOnRedisImpl, BudgetRepositoryOnRedisImpl, UserInfoRepositoryOnRedisImpl}
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.WinNoticeEndpoint
import com.github.BambooTuna.LoadTest.usecase.{GetAdvertiserIdUseCase, _}

object Routes {

  val logger = LoggerFactory.getLogger(getClass)

  def createRouter(jdbcSetting: JdbcSetting, redisSettings: RedisSetting, aerospikeSetting: AerospikeSetting)(
      implicit system: ActorSystem,
      materializer: ActorMaterializer
  ): Router = {

    val userInfoRepositoryBalancer = UserInfoRepositoryBalancer(Seq(new UserInfoRepositoryOnRedisImpl(
      redisSettings.copy(redis_db = Some(2)).client
    )))

    val budgetRepositoryBalancer = BudgetRepositoryBalancer(Seq(new BudgetRepositoryOnJDBCImpl(
      jdbcSetting.client
    )))

    val advertiserIdRepositoryBalancer = AdvertiserIdRepositoryBalancer(Seq(new AdvertiserIdRepositoryOnRedisImpl(
      redisSettings.copy(redis_db = Some(3)).client
    )))

    commonRouter +
      bidRequestRouter(userInfoRepositoryBalancer)(budgetRepositoryBalancer) +
      winNoticeRouter(budgetRepositoryBalancer)(advertiserIdRepositoryBalancer) +
      addUserInfoRoute(userInfoRepositoryBalancer) +
      setBudgetRoute(budgetRepositoryBalancer)
  }

  def commonRouter(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET, "", CommonRoute().top),
      route(GET, "ping", CommonRoute().ping)
    )

  def bidRequestRouter(userInfoRepositoryBalancer: UserInfoRepositoryBalancer[UserInfoDao])
                      (budgetRepositoryBalancer: BudgetRepositoryBalancer[BudgetDao])(
      implicit system: ActorSystem,
      materializer: ActorMaterializer
  ): Router = {
    val getUserInfoUseCase = GetUserInfoUseCase(userInfoRepositoryBalancer)
    val getBudgetUseCase = GetBudgetUseCase(budgetRepositoryBalancer)
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
            WinNoticeEndpoint("http://localhost:8080/win") //TODO
          )
        ).route
      )
    )
  }

  def winNoticeRouter(budgetRepositoryBalancer: BudgetRepositoryBalancer[BudgetDao])
                     (advertiserIdRepositoryBalancer: AdvertiserIdRepositoryBalancer[AdvertiserIdDao])(
      implicit system: ActorSystem,
      materializer: ActorMaterializer
  ): Router = {
    val getAdvertiserIdUseCase = GetAdvertiserIdUseCase(advertiserIdRepositoryBalancer)
    val reduceBudgetFromWinNoticeUseCase = ReduceBudgetFromWinNoticeUseCase(budgetRepositoryBalancer)(getAdvertiserIdUseCase)
    Router(
      route(POST, "win", ReduceBudgetFromWinNoticeRoute(reduceBudgetFromWinNoticeUseCase).route)
    )
  }

  def addUserInfoRoute(userInfoRepositoryBalancer: UserInfoRepositoryBalancer[UserInfoDao])(
    implicit system: ActorSystem,
    materializer: ActorMaterializer
  ): Router = {
    val addUserInfoUseCase = AddUserInfoUseCase(userInfoRepositoryBalancer)
    Router(
      route(POST, "user" / "add", AddUserInfoRoute(addUserInfoUseCase).route)
    )
  }

  def setBudgetRoute(budgetRepositoryBalancer: BudgetRepositoryBalancer[BudgetDao])(
    implicit system: ActorSystem,
    materializer: ActorMaterializer
  ): Router = {
    val setBudgetUseCase: SetBudgetUseCase = SetBudgetUseCase(budgetRepositoryBalancer)
    Router(
      route(POST, "budget" / "set", SetBudgetRoute(setBudgetUseCase).route)
    )
  }

}
