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
  .settings(jvmSettings)
  .settings(
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
    name := "gatling-test",
    libraryDependencies ++= Seq(
      Circe.core,
      Circe.generic,
      Circe.parser,
    ) ++ Gatling.all,
    publishArtifact in(GatlingIt, packageBin) := true
  )
  .settings(addArtifact(artifact in(GatlingIt, packageBin), packageBin in GatlingIt))
  .dependsOn(interface)

lazy val `gatling-runner` = (project in file("tools/gatling-runner"))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .enablePlugins(GatlingPlugin)
  .settings(commonSettings)
  .settings(gatlingCommonSettings)
  .settings(jvmSettings)
  .settings(
    name := "gatling-runner",
    libraryDependencies ++= Seq(
      GoogleAPIs.cloudStorage
    ) ++ Gatling.all
  )
  .dependsOn(
    `gatling-test` % "compile->gatling-it"
  )

lazy val root =
  (project in file("."))
    .aggregate(boot, `gatling-test`, `gatling-runner`)