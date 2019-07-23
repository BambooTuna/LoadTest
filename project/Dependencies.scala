import sbt._

object ScalaTest {
  val version = "org.scalatest" %% "scalatest" % "3.0.5"
}

object ScalaCheck {
  val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.0"
}

object ScalaMock {
  val version = "org.scalamock" %% "scalamock" % "4.1.0"
}

object Gatling {
  private val version = "3.1.3"
  
  val charts = "io.gatling.highcharts" % "gatling-charts-highcharts" % version % "test,it"
  val framework = "io.gatling" % "gatling-test-framework" % version % "test,it"
  
  val all = Seq(charts, framework)
}

object Akka {
  private val version     = "2.5.19"
  val actor: ModuleID     = "com.typesafe.akka" %% "akka-actor" % version
  val stream: ModuleID    = "com.typesafe.akka" %% "akka-stream" % version
  val testkit: ModuleID   = "com.typesafe.akka" %% "akka-testkit" % version
  val slf4j: ModuleID     = "com.typesafe.akka" %% "akka-slf4j" % version
  val experimental: ModuleID = "com.typesafe.akka" % "akka-http-experimental_2.11" % "2.4.11.2"
  private val httpVersion = "10.1.7"
  val http                = "com.typesafe.akka" %% "akka-http" % httpVersion
  val httpTestKit         = "com.typesafe.akka" %% "akka-http-testkit" % httpVersion

  val `akka-http-crice` = "de.heikoseeberger" %% "akka-http-circe" % "1.24.3"
}

object Circe {
  private val version   = "0.11.1"
  val core: ModuleID    = "io.circe" %% "circe-core" % version
  val parser: ModuleID  = "io.circe" %% "circe-parser" % version
  val generic: ModuleID = "io.circe" %% "circe-generic" % version
  val extras: ModuleID  = "io.circe" %% "circe-generic-extras" % version

}

object Logback {
  private val version   = "1.2.3"
  val classic: ModuleID = "ch.qos.logback" % "logback-classic" % version
}

object LogstashLogbackEncoder {
  private val version = "4.11"
  val encoder = "net.logstash.logback" % "logstash-logback-encoder" % version excludeAll (
    ExclusionRule(organization = "com.fasterxml.jackson.core", name = "jackson-core"),
    ExclusionRule(organization = "com.fasterxml.jackson.core", name = "jackson-databind")
  )
}

object ScalaLogging {
  private val version      = "3.5.0"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % version
}

object Enumeratum {
  val version = "com.beachape" %% "enumeratum-circe" % "1.5.21"
}

object JWT {
  val core = "com.pauldijou" %% "jwt-core" % "2.1.0"
}

object Config {
  private val version = "1.3.4"
  val core = "com.typesafe" % "config" % version
}

object Airframe {
  private val version = "0.80"
  val di              = "org.wvlet.airframe" %% "airframe" % version
}

object Slick {
  private val version            = "3.3.0"
  val slick: ModuleID    = "com.typesafe.slick" %% "slick" % version
  val hikaricp: ModuleID = "com.typesafe.slick" %% "slick-hikaricp" % version
}

object MySQL {
  private val version            = "6.0.6"
  val connector: ModuleID = "mysql" % "mysql-connector-java" % version
}

object Kamon {
  private val version = "0.6.7"

  val core = "io.kamon" %% "kamon-core" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val autoweave = "io.kamon" %% "kamon-autoweave" % "0.6.5" excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val systemMetrics = "io.kamon" %% "kamon-system-metrics" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val scala = "io.kamon" %% "kamon-scala" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val akka = "io.kamon" %% "kamon-akka-2.5" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val akkaHttp = "io.kamon" %% "kamon-akka-http" % "0.6.8" excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules"),
    ExclusionRule(organization = "com.typesafe.akka")
  )

  val datadog = "io.kamon" %% "kamon-datadog" % version excludeAll (
    ExclusionRule(organization = "org.asynchttpclient"), // awsclientのものとバッティングするため
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val jmx = "io.kamon" %% "kamon-jmx" % version excludeAll (
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j"),
    ExclusionRule(organization = "org.scala-lang.modules")
  )

  val all = Seq(core, autoweave, systemMetrics, scala, akka, akkaHttp, datadog/*, jmx */)
}

object AspectjLib {
  val aspectjweaver = "org.aspectj" % "aspectjweaver" % "1.8.10"
}


object DDDBase {
  private val scalaDddBaseVersion = "1.0.27"
  
  val core = "com.github.j5ik2o" %% "scala-ddd-base-core" % scalaDddBaseVersion
  val slick = "com.github.j5ik2o" %% "scala-ddd-base-slick" % scalaDddBaseVersion
  val redis = "com.github.j5ik2o" %% "scala-ddd-base-redis" % scalaDddBaseVersion
  val dynamo = "com.github.j5ik2o" %% "scala-ddd-base-dynamodb" % scalaDddBaseVersion
}