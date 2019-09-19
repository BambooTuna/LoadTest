package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.CommandResponse
import kamon.Kamon
import kamon.metric.instrument.Gauge
import monix.eval.Task
import org.slf4j.LoggerFactory

trait UseCaseCommon {

  val logger = LoggerFactory.getLogger(getClass)

  private val requestCounter = Kamon.metrics.counter(s"${getClass.getName}-request")
  private val successCounter = Kamon.metrics.counter(s"${getClass.getName}-success")
  private val failedCounter  = Kamon.metrics.counter(s"${getClass.getName}-failed")
  private val responseTimer =
    Kamon.metrics.gauge(s"${getClass.getName}-response")(Gauge.functionZeroAsCurrentValueCollector(() => 0L))

  private var time = java.time.Instant.now()

  def setResponseTimer: Task[Unit] =
    Task.pure {
      requestCounterIncrement()
      time = java.time.Instant.now()
    }

  private def requestCounterIncrement() =
    requestCounter.increment()

  private def recodeResponseTime() = {
    val responseTime = java.time.Instant.now().toEpochMilli - time.toEpochMilli
    responseTimer.record(responseTime)
    logger.debug(s"${getClass.getName}-responseTime: $responseTime")
  }

  private def successCounterIncrement[T](any: T): T = {
    successCounter.increment()
    recodeResponseTime()
    logger.debug(s"${getClass.getName}-success")
    any
  }

  private def failedCounterIncrement[T](mes: String)(failedCommand: String => T): T = {
    failedCounter.increment()
    recodeResponseTime()
    logger.debug(s"${getClass.getName}-failed: $mes")
    failedCommand(mes)
  }

  implicit class MetricHandler[R](result: Task[R]) {
    def responseHandle[C <: CommandResponse](successCommand: R => C)(failedCommand: String => C): Task[C] = {
      result
        .map(successCounterIncrement(successCommand)(_))
        .onErrorHandle { ex =>
          failedCounterIncrement(ex.getMessage)(failedCommand)
        }
    }
  }

}
