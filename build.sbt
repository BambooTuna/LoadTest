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
  )
  .dependsOn(domain, infrastructure)

lazy val interface = (project in file("interface"))
  .settings(commonSettings)
  .settings(
    name := "LoadTest-interface",
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(useCase, infrastructure)

lazy val boot = (project in file("boot"))
  .settings(commonSettings)
  .settings(
    name := "LoadTest-boot",
    libraryDependencies ++= Seq(
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