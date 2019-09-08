package com.github.BambooTuna.LoadTest.boot.server

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.{ GET, POST, PUT }
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.server.Directives._
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc.JdbcSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.{ OnRedisClient, OnSlickClient }
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.RedisSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.{
  AdIdRepositoryOnRedisImpl,
  BudgetRepositoryOnRedisImpl,
  UserRepositoryOnRedisImpl
}
import com.github.BambooTuna.LoadTest.domain.model.ad.WinRedirectUrl
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.AddUserCommandRequest
import com.github.BambooTuna.LoadTest.usecase.{ AddUserUseCase, AddWinUseCase, _ }
import com.github.BambooTuna.LoadTest.usecase.calculate.{ CalculateModelUseCase, CalculateModelUseCaseImpl }
import com.github.BambooTuna.LoadTest.usecase.json.UserDataJson
import monix.eval.Task

import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future
import scala.io.Source
import scala.util.Try

object Routes {

  val logger = LoggerFactory.getLogger(getClass)

  def createRouter(redisSettings: Seq[RedisSetting])(
      implicit system: ActorSystem,
      materializer: ActorMaterializer
  ): Router = {

//    val slickClient: OnSlickClient       = jdbcSetting.client
    val redisClients: Seq[OnRedisClient] = redisSettings.map(_.client)

    val clientCluster: ClientCluster = ClientCluster(redisClients)

    commonRouter +
    adPro(clientCluster)
  }

  def commonRouter(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET, "", CommonRoute().top),
      route(GET, "ping", CommonRoute().ping)
    )

  def adPro(clientCluster: ClientCluster)(implicit materializer: ActorMaterializer): Router = {
    val (userRedisClients, o)                  = clientCluster.redisClients.splitAt(3)
    val (adidRedisClients, budgetRedisClients) = o.splitAt(3)

    require(userRedisClients.size == 3 && adidRedisClients.size == 3, budgetRedisClients.size == 1)

    val userRepositories = userRedisClients.map(new UserRepositoryOnRedisImpl(_))
    val adidRepository   = adidRedisClients.map(new AdIdRepositoryOnRedisImpl(_))
    val budgetRepository = budgetRedisClients.map(new BudgetRepositoryOnRedisImpl(_))

    val getUserUseCase: GetUserUseCase = GetUserUseCaseImpl(
      GetUserRepositoryBalance(userRepositories)
    )

    val getBudgetUseCase: GetBudgetUseCase = GetBudgetUseCaseImpl(
      GetBudgetRepositoryBalance(budgetRepository)
    )

    val getAdIdUseCase: GetAdIdUseCase = GetAdIdUseCaseImpl(
      GetAdIdRepositoryBalance(adidRepository)
    )
    val addAdIdUseCase: AddAdIdUseCase = AddAdIdUseCaseImpl(
      GetAdIdRepositoryBalance(adidRepository)
    )

    val addWinUseCase: AddWinUseCase = AddWinUseCaseImpl(
      budgetRepository.head,
      getAdIdUseCase
    )

    val addUserUseCase: AddUserUseCase = AddUserUseCaseImpl(
      GetUserRepositoryBalance(userRepositories)
    )

    val setBudgetUseCase: SetBudgetUseCase = SetBudgetUseCaseImpl(
      GetBudgetRepositoryBalance(budgetRepository)
    )

    val calculateModelUseCase: CalculateModelUseCase = CalculateModelUseCaseImpl()
    val getModelUseCase: GetModelUseCase             = GetModelUseCaseImpl(calculateModelUseCase)

    Router(
      route(
        POST,
        "bid_request",
        BidRoute(
          BidUseCaseImpl(
            getUserUseCase,
            addAdIdUseCase,
            getBudgetUseCase,
            getModelUseCase,
            WinRedirectUrl("http://34.84.137.136:8080/win") //TODO
          )
        ).route
      ),
      route(POST, "win", WinResultRoute(addWinUseCase).route),
      route(POST,
            "user" / "add",
            AddUserRoute(
              addUserUseCase
            ).route),
      route(POST,
            "budget" / "set",
            SetBudgetRoute(
              setBudgetUseCase
            ).route),
      route(
        PUT,
        "setup", {
          val f = Task {
            SetupRedis.addDataToRedis(addUserUseCase)
            Thread.sleep(999999999999L)
          }.runToFuture
          onComplete(f) {
            case _ => complete(StatusCodes.OK)
          }
        }
      )
    )
  }

}
