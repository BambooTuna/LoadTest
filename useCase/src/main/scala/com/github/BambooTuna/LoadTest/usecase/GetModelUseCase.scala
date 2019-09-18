package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{ GetModelCommandRequest, GetModelCommandResponse }
import com.github.BambooTuna.LoadTest.usecase.calculate.CalculateModelUseCase
import monix.eval.Task

trait GetModelUseCase extends UseCaseCommon {

  def run(arg: GetModelCommandRequest): Task[GetModelCommandResponse]

  def runWithOutSide(arg: GetModelCommandRequest)(implicit system: ActorSystem,
                                                  mat: Materializer): Task[GetModelCommandResponse] = ???

}
