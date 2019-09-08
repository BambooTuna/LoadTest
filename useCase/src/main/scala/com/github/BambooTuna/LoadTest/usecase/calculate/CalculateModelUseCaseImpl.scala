package com.github.BambooTuna.LoadTest.usecase.calculate

import com.github.BambooTuna.LoadTest.usecase.json.{ GetModelRequestJson, GetModelResponseJson }
import monix.eval.Task

case class CalculateModelUseCaseImpl() extends CalculateModelUseCase {

  override def run(arg: GetModelRequestJson): Task[GetModelResponseJson] = Task {
    arg.bid_log
    arg.footprint_log
    //TODO
    GetModelResponseJson(0.05)
  }

}
