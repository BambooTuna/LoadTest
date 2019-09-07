package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.AdIdRepository
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{ GetAdIdCommandRequest, GetAdIdCommandResponse }
import monix.eval.Task

trait GetAdIdUseCase extends UseCaseCommon {

  val adidRepositories: GetAdIdRepositoryBalance[AdIdRepository]

  def run(arg: GetAdIdCommandRequest): Task[GetAdIdCommandResponse]

}
