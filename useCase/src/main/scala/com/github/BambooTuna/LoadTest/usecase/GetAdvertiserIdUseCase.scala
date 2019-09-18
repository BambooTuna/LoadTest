package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.AdvertiserIdRepository
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{ GetAdvertiserIdCommandRequest, GetAdvertiserIdCommandResponse }
import monix.eval.Task

trait GetAdvertiserIdUseCase extends UseCaseCommon {

  val advertiserIdRepositories: AdvertiserIdRepositoryBalancer[AdvertiserIdRepository]

  def run(arg: GetAdvertiserIdCommandRequest): Task[GetAdvertiserIdCommandResponse]

}
