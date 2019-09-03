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
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(commonSettings)
  .settings(dockerSettings)
  .settings(
    libraryDependencies ++= Seq(
      Akka.`akka-http-crice`
    )
      ++ Kamon.all
  )
  .settings(
    fork := true,
    javaOptions in Universal ++= Seq(
      "-server",
      "-Djava.rmi.server.hostname=127.0.0.1",
      s"-Dcom.sun.management.jmxremote.rmi.port=${sys.env.getOrElse("JMX_PORT", "8999")}",
      "-Dcom.sun.management.jmxremote.ssl=false",
      "-Dcom.sun.management.jmxremote.local.only=false",
      "-Dcom.sun.management.jmxremote.authenticate=false",
      "-Dcom.sun.management.jmxremote",
      s"-Dcom.sun.management.jmxremote.port=${sys.env.getOrElse("JMX_PORT", "8999")}"
    ),
  )
  .dependsOn(interface, infrastructure)

lazy val `gatling-test` = (project in file("tools/gatling-test"))
  .settings(commonSettings)
//  .settings(gatlingCommonSettings)
  .enablePlugins(GatlingPlugin)
  .settings(
    name := "LoadTest-gatling-test",
    version := "0.1",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      Circe.core,
      Circe.generic,
      Circe.parser,
    ) ++ Gatling.all
  )
  .dependsOn(interface)

lazy val root =
  (project in file("."))
    .aggregate(boot, `gatling-test`)