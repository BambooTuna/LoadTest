package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.AdvertiserIdDao
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol._
import monix.eval.Task

case class GetAdvertiserIdUseCase(advertiserIdRepositories: AdvertiserIdRepositoryBalancer[AdvertiserIdDao])
    extends UseCaseCommon {

  def run(arg: GetAdvertiserIdCommandRequest): Task[GetAdvertiserIdCommandResponse] = {
    (for {
      _ <- setResponseTimer
      aggregate <- Task.pure(
        arg.id
      )
      r <- advertiserIdRepositories.getConnectionWithAdRequestId(aggregate).resolveById(aggregate)
    } yield r)
      .map(_.get)
      .responseHandle[GetAdvertiserIdCommandResponse](GetAdvertiserIdCommandSucceeded)(GetAdvertiserIdCommandFailed)
  }

}
