package com.github.BambooTuna.LoadTest.boot.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.{ Config, ConfigFactory }
import kamon.Kamon
import org.slf4j.LoggerFactory

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
  val route         = Routes.route
  val bindingFuture = Http().bindAndHandle(route, serverConfig.host, serverConfig.port)

  sys.addShutdownHook {
    Kamon.shutdown()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
