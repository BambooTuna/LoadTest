package com.github.BambooTuna.LoadTest.boot.server

import java.io.File

import akka.NotUsed
import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.model.HttpMethods.{GET, POST, PUT}
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}
import com.aerospike.client.AerospikeClient
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.aerospike.AerospikeSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc.JdbcSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.{OnAerospikeClient, OnRedisClient, OnSlickClient}
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.RedisSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.{AdvertiserIdRepositoryOnRedisImpl, BudgetRepositoryOnRedisImpl, UserInfoRepositoryOnRedisImpl}
import com.github.BambooTuna.LoadTest.boot.server.SetupRedis.tryProcessSource
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.WinNoticeEndpoint
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.AddUserCommandRequest
import com.github.BambooTuna.LoadTest.usecase.{AddUserInfoUseCase, AddWinUseCase, _}
import com.github.BambooTuna.LoadTest.usecase.calculate.{CalculateModelUseCase, CalculateModelUseCaseImpl}
import com.github.BambooTuna.LoadTest.usecase.json.UserDataJson
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future
import scala.io.Source
import scala.util.Try

object Routes {

  val logger = LoggerFactory.getLogger(getClass)

  def createRouter(jdbcSetting: JdbcSetting, redisSettings: RedisSetting, aerospikeSetting: AerospikeSetting)(
      implicit system: ActorSystem,
      materializer: ActorMaterializer
  ): Router = {

    val mysqlClient: OnSlickClient       = jdbcSetting.client
    val redisClient: OnRedisClient = redisSettings.client
    val aerospikeClient: OnAerospikeClient = aerospikeSetting.client

    commonRouter + dspServerRouter(mysqlClient, redisClient, aerospikeClient)
  }

  def commonRouter(implicit materializer: ActorMaterializer): Router =
    Router(
      route(GET, "", CommonRoute().top),
      route(GET, "ping", CommonRoute().ping)
    )

  def dspServerRouter(mysqlClient: OnSlickClient, redisClient: OnRedisClient, aerospikeClient: OnAerospikeClient)(implicit system: ActorSystem, materializer: ActorMaterializer): Router = {
    val (userRedisClients, o)                  = clientCluster.redisClients.splitAt(3)
    val (adidRedisClients, budgetRedisClients) = o.splitAt(3)

    require(userRedisClients.size == 3 && adidRedisClients.size == 3, budgetRedisClients.size == 1)

    val userRepositories = userRedisClients.map(new UserInfoRepositoryOnRedisImpl(_))
    val adidRepository   = adidRedisClients.map(new AdvertiserIdRepositoryOnRedisImpl(_))
    val budgetRepository = budgetRedisClients.map(new BudgetRepositoryOnRedisImpl(_))

    val getUserUseCase: GetUserInfoUseCase = GetUserInfoUseCaseImpl(
      UserInfoRepositoryBalancer(userRepositories)
    )

    val getBudgetUseCase: GetBudgetUseCase = GetBudgetUseCaseImpl(
      BudgetRepositoryBalancer(budgetRepository)
    )

    val getAdIdUseCase: GetAdvertiserIdUseCase = GetAdvertiserIdUseCaseImpl(
      GetAdvertiserIdRepositoryBalancer(adidRepository)
    )
    val addAdIdUseCase: AddAdIdUseCase = AddAdIdUseCaseImpl(
      GetAdvertiserIdRepositoryBalancer(adidRepository)
    )

    val addWinUseCase: AddWinUseCase = AddWinUseCaseImpl(
      budgetRepository.head,
      getAdIdUseCase
    )

    val addUserUseCase: AddUserInfoUseCase = AddUserInfoUseCaseImpl(
      UserInfoRepositoryBalancer(userRepositories)
    )

    val setBudgetUseCase: SetBudgetUseCase = SetBudgetUseCaseImpl(
      BudgetRepositoryBalancer(budgetRepository)
    )

    val calculateModelUseCase: CalculateModelUseCase = CalculateModelUseCaseImpl()
    val getModelUseCase: GetModelUseCase             = GetModelUseCaseImpl(calculateModelUseCase)

//    val actor = system.actorOf(Props(classOf[SetDataActor], addUserUseCase), "SetDataActor")

    val tryLinesIrvingTxNoHeader: Try[List[List[String]]] =
      tryProcessSource(
        new File("/opt/docker/sample_user.csv"),
        parseLine = (index, unparsedLine) => Some(unparsedLine.split(",").toList),
        filterLine = (index, parsedValues) =>
          Some(
            index != 0 //skip header line
        )
      )

    tryLinesIrvingTxNoHeader.map(_.map {
      case List(a, b, c, d, e, f, g) =>
        val j = UserDataJson(a, b.toInt, c.toDouble, d.toDouble, e.toDouble, f.toDouble, g.toDouble)
        addUserUseCase.run(AddUserCommandRequest(j))
    })

    Router(
      route(
        POST,
        "bid_request",
        BidRequestRoute(
          BidRequestUseCaseImpl(
            getUserUseCase,
            addAdIdUseCase,
            getBudgetUseCase,
            getModelUseCase,
            WinNoticeEndpoint("http://34.84.137.136:8080/win") //TODO
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
        GET,
        "setup",
        extractActorSystem { implicit system =>
          extractRequestContext { c =>
            println("actor ! run")
//            actor ! "run"
            val f = Task {
              println("none task")
            }.runToFuture
            onSuccess(f) {
              case _ => c.complete(StatusCodes.OK)
            }
          }
        }
      )
    )
  }

}