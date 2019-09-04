import sbt.Keys._
import sbt._
import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._
import com.typesafe.sbt.SbtNativePackager.autoImport.{maintainer, packageName}
import com.typesafe.sbt.packager.archetypes.scripts.BashStartScriptPlugin.autoImport.{bashScriptDefines, bashScriptExtraDefines}
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._

object Settings {
  
  lazy val commonSettings = Seq(
    organization := "com.github.BambooTuna",
    scalaVersion := "2.12.8",
    version := "1.0.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      ScalaTest.version     % Test,
      ScalaCheck.scalaCheck % Test,
      ScalaMock.version     % Test,
      Enumeratum.version,
      Logback.classic,
      LogstashLogbackEncoder.encoder,
      Config.core,
      Airframe.di,
      Monix.version,
      MySQLConnectorJava.version,
      Redis.client,
    ) 
      ++ Aerospike.all,
    scalafmtOnCompile in Compile := true,
    scalafmtTestOnCompile in Compile := true
  )
  
  lazy val dockerSettings = Seq(
    fork := true,
    name := "loadtest",
    version := "latest",
    dockerBaseImage := "adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.4_11-slim",
    maintainer in Docker := "BambooTuna <bambootuna@gmail.com>",
    dockerUpdateLatest := true,
    dockerUsername := Some("bambootuna"),
    mainClass in (Compile, bashScriptDefines) := Some("com.github.BambooTuna.LoadTest.boot.server.Main"),
    packageName in Docker := name.value,
    dockerExposedPorts := Seq(8080, 8999),
    bashScriptExtraDefines ++= Seq(
      {
        val revision =
          libraryDependencies.value.find(m => m.organization == "org.aspectj" && m.name == "aspectjweaver").get.revision
        s"""addJava "-javaagent:$${lib_dir}/org.aspectj.aspectjweaver-$revision.jar""""
      },
//      s"""addJava "-Xms${sys.env.getOrElse("JVM_HEAP_MIN", "${JVM_HEAP_MIN:-1024m}")}"""",
//      s"""addJava "-Xmx${sys.env.getOrElse("JVM_HEAP_MAX", "${JVM_HEAP_MAX:-1024m}")}"""",
//      s"""addJava "-XX:MaxMetaspaceSize=${sys.env.getOrElse("JVM_META_MAX", "${JVM_META_MAX:-512m}")}"""",
      s"""addJava "${sys.env.getOrElse("JVM_GC_OPTIONS", "${JVM_GC_OPTIONS:--XX:+UseG1GC}")}""""
    ),
//    dockerCommands ++= Seq(
//      Cmd("ENV", "DATADOG_HOSTNAME", "datadog"),
//      Cmd("ENV", "DATADOG_PORT", "8125")
//    )
  )

  lazy val gatlingCommonSettings = Seq(
    fork := true,
    name := "loadtest-gatling",
    version := "latest",
    dockerBaseImage := "adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.4_11-slim",
    maintainer in Docker := "BambooTuna <bambootuna@gmail.com>",
    dockerUpdateLatest := true,
    dockerUsername := Some("bambootuna"),
    mainClass in (Compile, bashScriptDefines) := Some("com.github.BambooTuna.LoadTest.gatling.runner.Runner"),
    packageName in Docker := name.value,
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding",
      "UTF-8",
      "-Xfatal-warnings",
      "-language:_",
      // Warn if an argument list is modified to match the receiver
      "-Ywarn-adapted-args",
      // Warn when dead code is identified.
      "-Ywarn-dead-code",
      // Warn about inaccessible types in method signatures.
      "-Ywarn-inaccessible",
      // Warn when a type argument is inferred to be `Any`.
      "-Ywarn-infer-any",
      // Warn when non-nullary `def f()' overrides nullary `def f'
      "-Ywarn-nullary-override",
      // Warn when nullary methods return Unit.
      "-Ywarn-nullary-unit",
      // Warn when numerics are widened.
      "-Ywarn-numeric-widen",
      // Warn when imports are unused.
      "-Ywarn-unused-import",
      "-Ywarn-numeric-widen"
    )
  )

}
