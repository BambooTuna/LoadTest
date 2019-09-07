package com.github.BambooTuna.LoadTest.boot.server

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods.{GET, POST, PUT}
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.server.Directives._
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc.JdbcSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.{OnRedisClient, OnSlickClient}
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.RedisSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.{AdIdRepositoryOnRedisImpl, BudgetRepositoryOnRedisImpl, UserRepositoryOnRedisImpl}
import com.github.BambooTuna.LoadTest.domain.model.ad.WinRedirectUrl
import com.github.BambooTuna.LoadTest.usecase.{AddUserUseCase, AddWinUseCase, _}
import com.github.BambooTuna.LoadTest.usecase.calculate.{CalculateModelUseCase, CalculateModelUseCaseImpl}
import com.github.BambooTuna.LoadTest.usecase.json.UserDataJson

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
            ).route)
    )
  }

  def addDataToRedis = {
    val tryLinesIrvingTxNoHeader: Try[List[List[String]]] =
      tryProcessSource(
        new File("C:/Users/Jim/Desktop/test.csv"),
        parseLine = (index, unparsedLine) => Some(unparsedLine.split(",").toList),
      )

    tryLinesIrvingTxNoHeader.map(_.map{
      case List(device_id, advertiser_id, game_install_count, game_login_count, game_paid_count, game_tutorial_count, game_extension_count) =>
        UserDataJson(device_id, advertiser_id.toInt, game_install_count.toDouble, game_login_count.toDouble, game_paid_count.toDouble, game_tutorial_count.toDouble, game_extension_count.toDouble)
    })

  }


  import scala.io.Source
  import scala.util.Try

  import java.io.File

  def tryProcessSource(
      file: File,
      parseLine: (Int, String) => Option[List[String]] = (index, unparsedLine) => Some(List(unparsedLine)),
      filterLine: (Int, List[String]) => Option[Boolean] = (index, parsedValues) => Some(true),
      retainValues: (Int, List[String]) => Option[List[String]] = (index, parsedValues) => Some(parsedValues)
  ): Try[List[List[String]]] = {
    def usingSource[S <: Source, R](source: S)(transfer: S => R): Try[R] =
      try { Try(transfer(source)) } finally { source.close() }
    def recursive(
        remaining: Iterator[(String, Int)],
        accumulator: List[List[String]],
        isEarlyAbort: Boolean = false
    ): List[List[String]] = {
      if (isEarlyAbort || !remaining.hasNext)
        accumulator
      else {
        val (line, index) =
          remaining.next
        parseLine(index, line) match {
          case Some(values) =>
            filterLine(index, values) match {
              case Some(keep) =>
                if (keep)
                  retainValues(index, values) match {
                    case Some(valuesNew) =>
                      recursive(remaining, valuesNew :: accumulator) //capture values
                    case None =>
                      recursive(remaining, accumulator, isEarlyAbort = true) //early abort
                  } else
                  recursive(remaining, accumulator) //discard row
              case None =>
                recursive(remaining, accumulator, isEarlyAbort = true) //early abort
            }
          case None =>
            recursive(remaining, accumulator, isEarlyAbort = true) //early abort
        }
      }
    }
    Try(Source.fromFile(file)).flatMap(
      bufferedSource =>
        usingSource(bufferedSource) { source =>
          recursive(source.getLines().buffered.zipWithIndex, Nil).reverse
      }
    )
  }

}
