package com.github.BambooTuna.LoadTest.adaptor.routes

import kamon.Kamon
import kamon.metric.instrument.Gauge
import org.slf4j.LoggerFactory

class RouteCommonSetting {

  val logger = LoggerFactory.getLogger(getClass)

  val successCounter   = Kamon.metrics.counter(this.getClass.getName + "-success")
  val noContentCounter = Kamon.metrics.counter(this.getClass.getName + "-noContent")
  val errorCounter     = Kamon.metrics.counter(this.getClass.getName + "-error")
  val responseTime =
    Kamon.metrics.gauge(this.getClass.getName + "-responseTime")(Gauge.functionZeroAsCurrentValueCollector(() => 0L))

}
