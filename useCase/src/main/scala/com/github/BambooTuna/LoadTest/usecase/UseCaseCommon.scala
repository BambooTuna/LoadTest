package com.github.BambooTuna.LoadTest.usecase

import kamon.Kamon
import kamon.metric.instrument.Gauge
import org.slf4j.LoggerFactory

trait UseCaseCommon {

  val logger = LoggerFactory.getLogger(getClass)

  private val successCounter = Kamon.metrics.counter(s"${getClass.getName}-success")
  private val failedCounter  = Kamon.metrics.counter(s"${getClass.getName}-failed")
  private val responseTime =
    Kamon.metrics.gauge(s"${getClass.getName}-response")(Gauge.functionZeroAsCurrentValueCollector(() => 0L))

  private var time = java.time.Instant.now().toEpochMilli

  def setResponseTimer =
    time = java.time.Instant.now().toEpochMilli

  def successCounterIncrement = {
    successCounter.increment()
    responseTime.record(java.time.Instant.now().toEpochMilli - time)
    logger.debug(s"${getClass.getName}-success")
  }

  def failedCounterIncrement = {
    failedCounter.increment()
    responseTime.record(java.time.Instant.now().toEpochMilli - time)
    logger.debug(s"${getClass.getName}-success")
  }

}
