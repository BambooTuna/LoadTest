package com.github.BambooTuna.LoadTest.boot.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.aerospike.AerospikeSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc.JdbcSetting
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis.RedisSetting
import com.typesafe.config.{ Config, ConfigFactory }
import kamon.Kamon
import org.slf4j.LoggerFactory
import ru.tinkoff.aerospike.dsl.SpikeImpl
import ru.tinkoff.aerospikeexamples.example.AClient
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object Main extends App {

  Kamon.start()

  val rootConfig: Config = ConfigFactory.load()

  implicit val system: ActorSystem                        = ActorSystem("loadtest", config = rootConfig)
  implicit val materializer: ActorMaterializer            = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger = LoggerFactory.getLogger(getClass)

  val serverConfig = ServerConfig(system.settings.config.getString("boot.server.host"),
                                  system.settings.config.getString("boot.server.port").toInt)

  val jdbcSetting: JdbcSetting = JdbcSetting(
    config = DatabaseConfig.forConfig[JdbcProfile](path = "slick", rootConfig)
  )

  val redisSetting = RedisSetting(
    host = system.settings.config.getString("redis.host"),
    port = system.settings.config.getInt("redis.port"),
    password = Some(system.settings.config.getString("redis.password")).filter(_.nonEmpty),
    redis_db = Some(system.settings.config.getInt("redis.db")),
    connectTimeout = Some(system.settings.config.getDuration("redis.connect-timeout").toMillis.millis)
  )

  val aerospikeSetting = AerospikeSetting(
    hosts = Seq(system.settings.config.getString("aerospike.host")),
    port = system.settings.config.getInt("aerospike.port"),
    namespace = system.settings.config.getString("aerospike.namespace"),
    setName = system.settings.config.getString("aerospike.setName"),
  )

  val route         = Routes.createRouter(jdbcSetting, redisSetting, aerospikeSetting).create
  val bindingFuture = Http().bindAndHandle(route, serverConfig.host, serverConfig.port)

  sys.addShutdownHook {
    Kamon.shutdown()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
