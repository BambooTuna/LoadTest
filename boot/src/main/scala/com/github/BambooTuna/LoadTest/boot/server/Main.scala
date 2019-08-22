package com.github.BambooTuna.LoadTest.boot.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.SlickProfile
import com.typesafe.config.{ Config, ConfigFactory }
import kamon.Kamon
import org.slf4j.LoggerFactory
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContextExecutor

object Main extends App {

  Kamon.start()

  val rootConfig: Config = ConfigFactory.load()

  implicit val system: ActorSystem                        = ActorSystem("loadtest", config = rootConfig)
  implicit val materializer: ActorMaterializer            = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger = LoggerFactory.getLogger(getClass)

  val serverConfig = ServerConfig(system.settings.config.getString("boot.server.host"),
                                  system.settings.config.getString("boot.server.port").toInt)

  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile](path = "slick", rootConfig)
  val slickProfile = new SlickProfile {
    override val profile = dbConfig.profile
    override val db      = dbConfig.db
  }

  val route         = Routes.createRouter(slickProfile).create
  val bindingFuture = Http().bindAndHandle(route, serverConfig.host, serverConfig.port)

  sys.addShutdownHook {
    Kamon.shutdown()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
