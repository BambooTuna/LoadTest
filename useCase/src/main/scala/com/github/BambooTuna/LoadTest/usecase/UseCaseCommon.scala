package com.github.BambooTuna.LoadTest.usecase

import java.time.Instant

import kamon.Kamon
import kamon.metric.instrument.Gauge
import org.slf4j.LoggerFactory

trait UseCaseCommon {

  val logger = LoggerFactory.getLogger(getClass)

  private val requestCounter = Kamon.metrics.counter(s"${getClass.getName}-request")
  private val failedCounter  = Kamon.metrics.counter(s"${getClass.getName}-failed")
  private val responseTimer =
    Kamon.metrics.gauge(s"${getClass.getName}-response")(Gauge.functionZeroAsCurrentValueCollector(() => 0L))

  private var time = java.time.Instant.now()

  def setResponseTimer = {
    requestCounterIncrement
    time = java.time.Instant.now()
  }

  private def requestCounterIncrement =
    requestCounter.increment()

  def recodeResponseTime = {
    val responseTime = java.time.Instant.now().toEpochMilli - time.toEpochMilli
    responseTimer.record(responseTime)
    logger.debug(s"${getClass.getName}-responseTime: $responseTime")
  }

  def failedCounterIncrement = {
    failedCounter.increment()
    logger.debug(s"${getClass.getName}-failed")
  }

}
