package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.AdIdRepository
import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.{ AddAdIdCommandRequest, AddAdIdCommandResponse }
import monix.eval.Task

trait AddAdIdUseCase extends UseCaseCommon {

  val adidRepositories: GetAdIdRepositoryBalance[AdIdRepository]

  def run(arg: AddAdIdCommandRequest): Task[AddAdIdCommandResponse]

}
