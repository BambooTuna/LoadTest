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

  val simulationClassName = config.getString("gatling.simulation-classname")

  val gatlingConfig = ConfigFactory.load("gatling.conf")
  val gatlingDir    = gatlingConfig.getString("gatling.core.directory.results")

  val dynamic                       = new ReflectiveDynamicAccess(getClass.getClassLoader)
  val clazz: Class[_ <: Simulation] = dynamic.getClassFor[Simulation](simulationClassName).get
  val simulationName                = clazz.getSimpleName

  println(s"Simulation class is: ${clazz.getCanonicalName}")
  println(s"Simulation name is: $simulationName")

  val props = new GatlingPropertiesBuilder
  props
    .simulationClass(clazz.getCanonicalName)

  Gatling.fromMap(props.build)

  val latestTimestamp = new File(gatlingDir).listFiles().map(_.getName.split("-")(1).toLong).max
  val targetLogFile   = s"$gatlingDir/${simulationName.toLowerCase()}-$latestTimestamp/simulation.log"

  println("generated gatling log file is " + targetLogFile)

  val keyName = s"test/${java.util.UUID.randomUUID()}.log"
  println(s"sending to bucket `$bucketName` with key `$keyName`")

  val gcs = GoogleCloudStorage(projectName, credentialFilePath)
  gcs.createBucket(bucketName, location).create(keyName, new FileInputStream(targetLogFile))

}
