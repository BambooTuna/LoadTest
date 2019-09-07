package com.github.BambooTuna.LoadTest.usecase

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{ BidCommandRequest, BidCommandResponse }
import monix.eval.Task

trait BidUseCase extends UseCaseCommon {

  def run(arg: BidCommandRequest)(implicit system: ActorSystem, mat: Materializer): Task[BidCommandResponse]

}
