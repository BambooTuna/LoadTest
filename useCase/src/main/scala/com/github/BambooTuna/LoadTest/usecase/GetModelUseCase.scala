package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{ GetModelCommandRequest, GetModelCommandResponse }
import com.github.BambooTuna.LoadTest.usecase.calculate.CalculateModelUseCase
import monix.eval.Task

trait GetModelUseCase extends UseCaseCommon {

  val calculateModelUseCase: CalculateModelUseCase

  def run(arg: GetModelCommandRequest): Task[GetModelCommandResponse]

  def runWithOutSide(arg: GetModelCommandRequest)(implicit system: ActorSystem,
                                                  mat: Materializer): Task[GetModelCommandResponse] = ???

}
