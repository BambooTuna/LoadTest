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
    ),
    scalafmtOnCompile in Compile := true,
    scalafmtTestOnCompile in Compile := true
  )
  lazy val dockerSettings = Seq(
    fork := true,
    name := "loadtest-server",
    version := "latest",
    dockerBaseImage := "adoptopenjdk/openjdk8:x86_64-alpine-jdk8u191-b12",
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
      s"""addJava "-Xms${sys.env.getOrElse("JVM_HEAP_MIN", "${JVM_HEAP_MIN:-1024m}")}"""",
      s"""addJava "-Xmx${sys.env.getOrElse("JVM_HEAP_MAX", "${JVM_HEAP_MAX:-1024m}")}"""",
      s"""addJava "-XX:MaxMetaspaceSize=${sys.env.getOrElse("JVM_META_MAX", "${JVM_META_MAX:-512m}")}"""",
      s"""addJava "${sys.env.getOrElse("JVM_GC_OPTIONS", "${JVM_GC_OPTIONS:--XX:+UseG1GC}")}""""
    ),
//    dockerCommands ++= Seq(
//      Cmd("ENV", "DATADOG_HOSTNAME", "datadog"),
//      Cmd("ENV", "DATADOG_PORT", "8125")
//    )
  )

}
