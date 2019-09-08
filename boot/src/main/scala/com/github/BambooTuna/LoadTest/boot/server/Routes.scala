package com.github.BambooTuna.LoadTest.boot.server

import java.io.File

import akka.NotUsed
import akka.actor.{ Actor, ActorSystem, Props }
import akka.http.scaladsl.model.HttpMethods.{ GET, POST, PUT }
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.routes._
import org.slf4j.LoggerFactory
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{ Flow, RunnableGraph, Sink, Source }
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc.JdbcSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.{ OnRedisClient, OnSlickClient }
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.RedisSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.repository.redis.{
  AdIdRepositoryOnRedisImpl,
  BudgetRepositoryOnRedisImpl,
  UserRepositoryOnRedisImpl
}
import com.github.BambooTuna.LoadTest.boot.server.SetupRedis.tryProcessSource
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

  def adPro(clientCluster: ClientCluster)(implicit system: ActorSystem, materializer: ActorMaterializer): Router = {
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
//
//class SetDataActor(addUserUseCase: AddUserUseCase) extends Actor {
//
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//
//  override def receive = {
//    case "run" =>
//      println("actor receive")
//
//      import monix.execution.Scheduler.Implicits.global
//      val tryLinesIrvingTxNoHeader: Try[List[List[String]]] =
//        tryProcessSource(
//          new File("/opt/docker/sample_user.csv"),
//          parseLine = (index, unparsedLine) => Some(unparsedLine.split(",").toList),
//          filterLine = (index, parsedValues) =>
//            Some(
//              index != 0 //skip header line
//          )
//        )
//
//      val source = Source[List[String]](
//        tryLinesIrvingTxNoHeader.get
//      )
//
//      val invert = Flow[List[String]].map {
//        case List(device_id,
//                  advertiser_id,
//                  game_install_count,
//                  game_login_count,
//                  game_paid_count,
//                  game_tutorial_count,
//                  game_extension_count) =>
//          UserDataJson(
//            device_id,
//            advertiser_id.toInt,
//            game_install_count.toDouble,
//            game_login_count.toDouble,
//            game_paid_count.toDouble,
//            game_tutorial_count.toDouble,
//            game_extension_count.toDouble
//          )
//      }
//      val sink = Sink.foreach[UserDataJson](json => {
//        addUserUseCase
//          .run(
//            AddUserCommandRequest(json)
//          )
//          .runToFuture
//      })
//
//      val runnable: RunnableGraph[NotUsed] = source via invert to sink
//      runnable.run()
//    case _ => ()
//
//  }
//
//}
