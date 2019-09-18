package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{BidRequestCommandRequest, BidRequestCommandResponse}
import monix.eval.Task

trait BidRequestUseCase extends UseCaseCommon {

  def run(arg: BidRequestCommandRequest)(implicit system: ActorSystem, mat: Materializer): Task[BidRequestCommandResponse]

}
