import Settings._

lazy val infrastructure = (project in file("infrastructure"))
  .settings(commonSettings)
  .settings(
    name := "LoadTest-infrastructure",
    libraryDependencies ++= Seq(
    )
  )

lazy val domain = (project in file("domain"))
  .settings(commonSettings)
  .settings(
    name := "LoadTest-domain",
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(infrastructure)

lazy val useCase = (project in file("useCase"))
  .settings(commonSettings)
  .settings(
    name := "LoadTest-useCase",
    libraryDependencies ++= Seq(
      Circe.core,
      Circe.generic,
      Circe.parser,
      Akka.http,
      Akka.stream,
      Akka.slf4j,
    )
      ++ Kamon.all
  )
  .dependsOn(domain, infrastructure)

lazy val interface = (project in file("interface"))
  .settings(commonSettings)
  .settings(
    name := "LoadTest-interface",
    resolvers += "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/", 
    libraryDependencies ++= Seq(
      Akka.`akka-http-crice`,
      DDDBase.core,
      DDDBase.slick
    )
  )
  .dependsOn(useCase, infrastructure)

lazy val boot = (project in file("boot"))
  .settings(commonSettings)
  .settings(
    name := "LoadTest-boot",
    libraryDependencies ++= Seq(
      Akka.`akka-http-crice`
    )
      ++ Kamon.all
  )
  .dependsOn(interface, infrastructure)

lazy val `gatling-test` = (project in file("tools/gatling-test"))
  .settings(commonSettings)
  .enablePlugins(GatlingPlugin)
  .settings(
    name := "LoadTest-gatling-test",
    version := "0.1",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
    ) ++ Gatling.all
  )

lazy val root =
  (project in file("."))
    .aggregate(boot, `gatling-test`)