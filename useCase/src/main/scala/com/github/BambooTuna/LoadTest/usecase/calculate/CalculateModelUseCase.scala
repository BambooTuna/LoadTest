package com.github.BambooTuna.LoadTest.usecase.calculate

import com.github.BambooTuna.LoadTest.usecase.UseCaseCommon
import com.github.BambooTuna.LoadTest.usecase.json.{ GetModelRequestJson, GetModelResponseJson }
import monix.eval.Task

trait CalculateModelUseCase extends UseCaseCommon {

  def run(arg: GetModelRequestJson): Task[GetModelResponseJson]

}
