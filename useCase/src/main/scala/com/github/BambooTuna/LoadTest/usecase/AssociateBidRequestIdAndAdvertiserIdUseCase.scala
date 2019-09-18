package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.AdvertiserIdDao
import com.github.BambooTuna.LoadTest.usecase.command.DspCommandProtocol.{
  AssociateBidRequestIdAndAdvertiserIdCommandFailed,
  AssociateBidRequestIdAndAdvertiserIdCommandRequest,
  AssociateBidRequestIdAndAdvertiserIdCommandResponse,
  AssociateBidRequestIdAndAdvertiserIdCommandSucceeded
}
import monix.eval.Task

case class AssociateBidRequestIdAndAdvertiserIdUseCase(repositories: AdvertiserIdRepositoryBalancer[AdvertiserIdDao])
    extends UseCaseCommon {

  def run(
      arg: AssociateBidRequestIdAndAdvertiserIdCommandRequest
  ): Task[AssociateBidRequestIdAndAdvertiserIdCommandResponse] = {
    (for {
      idAggregate <- Task.pure(
        arg.bidRequestId
      )
      recordAggregate <- Task.pure(
        arg.advertiserId
      )
      r <- repositories.getConnectionWithAdRequestId(idAggregate).insert(idAggregate, recordAggregate)
    } yield r)
      .map { _ =>
        AssociateBidRequestIdAndAdvertiserIdCommandSucceeded
      }.onErrorHandle { ex =>
        AssociateBidRequestIdAndAdvertiserIdCommandFailed(ex.getMessage)
      }

  }

}
