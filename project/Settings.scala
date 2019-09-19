import sbt.Keys._
import sbt._
import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._
import com.typesafe.sbt.SbtNativePackager.Universal
import com.typesafe.sbt.SbtNativePackager.autoImport.{maintainer, packageName}
import com.typesafe.sbt.packager.archetypes.scripts.BashStartScriptPlugin.autoImport.{bashScriptDefines, bashScriptExtraDefines}
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
object Settings {

  val sdk8 = "adoptopenjdk/openjdk8:x86_64-ubuntu-jdk8u212-b03-slim"
  val sdk11 = "adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.4_11-slim"
  
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
    scalafmtTestOnCompile in Compile := true,
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding",
      "UTF-8",
      "-Xfatal-warnings",
      "-language:_",
      // Warn if an argument list is modified to match the receiver
//      "-Ywarn-adapted-args",
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
//      "-Ywarn-numeric-widen",
      // Warn when imports are unused.
      "-Ywarn-unused-import"
    )
  )
  
  lazy val dockerSettings = Seq(
    fork := true,
    name := "loadtest",
    version := "latest",
    dockerBaseImage := sdk11,
    maintainer in Docker := "BambooTuna <bambootuna@gmail.com>",
    dockerUpdateLatest := true,
    dockerUsername := Some("bambootuna"),
    mainClass in (Compile, bashScriptDefines) := Some("com.github.BambooTuna.LoadTest.boot.server.Main"),
    packageName in Docker := name.value,
    dockerExposedPorts := Seq(8080),
    mappings in Universal += {
      file(s"${sys.env.getOrElse("REDIS_USER_CSV_PATH", "sample_user.csv")}") -> "sample_user.csv"
    },
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
    name := "adtech-compe-2019-d-loadtest-gatling",
    version := "latest",
    //JDLの負荷試験の成績[20 ms < t < 50 ms](sdk8 : sdk11 = 2 : 1 = 40% : 20%)
    dockerBaseImage := sdk8,
    maintainer in Docker := "BambooTuna <bambootuna@gmail.com>",
    dockerUpdateLatest := true,
    dockerUsername := Some("cyberagenthack"),
    mainClass in (Compile, bashScriptDefines) := Some("com.github.BambooTuna.LoadTest.gatling.runner.Runner"),
    packageName in Docker := name.value,
    mappings in Universal += {
      file(s"${sys.env.getOrElse("LOCAL_CREDENTIAL_PATH", "infrastructure/staging/gcp/terraform/account.json")}") -> "account.json"
    },
//    IPv6（Javaでデフォルトで有効になっている）がパフォーマンスの問題を引き起こすことがあるため
//    javaOptions in Universal ++= Seq(
//      "-Djava.net.preferIPv4Stack=true",
//      "-Djava.net.preferIPv6Addresses=false"
//    ),
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

  lazy val jvmSettings = Seq(
    fork := true,
    dockerExposedPorts := Seq(8999),
    javaOptions in Universal ++= Seq(
      "-server",
      "-Djava.rmi.server.hostname=127.0.0.1",
      s"-Dcom.sun.management.jmxremote.rmi.port=${sys.env.getOrElse("JMX_PORT", "8999")}",
      "-Dcom.sun.management.jmxremote.ssl=false",
      "-Dcom.sun.management.jmxremote.local.only=false",
      "-Dcom.sun.management.jmxremote.authenticate=false",
      "-Dcom.sun.management.jmxremote",
      s"-Dcom.sun.management.jmxremote.port=${sys.env.getOrElse("JMX_PORT", "8999")}"
    )
  )

}
