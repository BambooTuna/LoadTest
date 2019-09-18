package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.AdvertiserIdRepository
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.eval.Task

case class GetAdvertiserIdUseCaseImpl(advertiserIdRepositories: AdvertiserIdRepositoryBalancer[AdvertiserIdRepository]) extends GetAdvertiserIdUseCase {

  override def run(arg: GetAdvertiserIdCommandRequest): Task[GetAdvertiserIdCommandResponse] = {
    (for {
      aggregate <- Task.pure(
        arg.request
      )
      r <- advertiserIdRepositories.getConnectionWithAdRequestId(arg.request).get(aggregate)
    } yield r)
      .map { result =>
        GetAdvertiserIdCommandSucceeded(result.get._2)
      }.onErrorHandle { ex =>
        GetAdvertiserIdCommandFailed(ex.getMessage)
      }

  }

}
