package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.AdvertiserIdRepository
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{ AddAdIdCommandRequest, AddAdIdCommandResponse }
import monix.eval.Task

trait AddAdIdUseCase extends UseCaseCommon {

  val adidRepositories: AdvertiserIdRepositoryBalancer[AdvertiserIdRepository]

  def run(arg: AddAdIdCommandRequest): Task[AddAdIdCommandResponse]

}
