package com.github.BambooTuna.LoadTest.boot.server

import monix.eval.Task
import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global


object TaskTest extends App {

  val task1 = Task.sleep(1.seconds)
  val task2 = Task.sleep(2.seconds)

  Task.race(task1, task2).runToFuture

}
