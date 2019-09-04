package com.github.BambooTuna.LoadTest.gatling.runner

import java.io.{ File, FileInputStream }

import akka.actor.ReflectiveDynamicAccess
import com.typesafe.config.ConfigFactory
import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder
import io.gatling.core.scenario.Simulation

object Runner extends App {

  val config = ConfigFactory.load()

  val credentialFilePath = config.getString("gatling.gcp.credential")
  val projectName        = config.getString("gatling.gcp.project-name")
  val location           = config.getString("gatling.gcp.location")
  val bucketName         = config.getString("gatling.gcp.bucket-name")

  val simulationClassNames = config.getStringList("gatling.simulation-classname")

  val gatlingConfig = ConfigFactory.load("gatling.conf")
  val gatlingDir    = gatlingConfig.getString("gatling.core.directory.results")

  val dynamic = new ReflectiveDynamicAccess(getClass.getClassLoader)

  simulationClassNames.forEach(run)

  def run(simulationClassName: String) = {

    val clazz: Class[_ <: Simulation] = dynamic.getClassFor[Simulation](simulationClassName).get
    val simulationName                = clazz.getSimpleName

    val props = new GatlingPropertiesBuilder
    props
      .simulationClass(clazz.getCanonicalName)
    Gatling.fromMap(props.build)

    val latestTimestamp = new File(gatlingDir).listFiles().map(_.getName.split("-")(1).toLong).max
    val targetLogFile   = s"$gatlingDir/${simulationName.toLowerCase()}-$latestTimestamp/simulation.log"
    val keyName         = s"gatling_log/${simulationName.toLowerCase()}/${java.util.UUID.randomUUID()}.log"

    val gcs = GoogleCloudStorage(projectName, credentialFilePath)
    gcs
      .createBucket(bucketName, location)
      .create(
        keyName,
        new FileInputStream(targetLogFile)
      )

  }
  Thread.sleep(1000000)

}
