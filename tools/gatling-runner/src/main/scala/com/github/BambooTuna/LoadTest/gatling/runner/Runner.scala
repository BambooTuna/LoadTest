package com.github.BambooTuna.LoadTest.gatling.runner

import java.io.File

import akka.actor.ReflectiveDynamicAccess
import com.typesafe.config.ConfigFactory
import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder
import io.gatling.core.scenario.Simulation

object Runner extends App {

  val config = ConfigFactory.load()

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

}
