package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.AdvertiserIdDao
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.BidRequestId
import kamon.Kamon
import org.slf4j.LoggerFactory

case class AdvertiserIdRepositoryBalancer[+T <: AdvertiserIdDao](dbs: Seq[T]) {
  require(dbs.nonEmpty)

  val logger = LoggerFactory.getLogger(getClass)

  lazy val dbCount = dbs.size

  val budgetRepositories           = (1 to dbCount).map(i => Kamon.metrics.counter(s"GetAdvertiserIdRepository-$i"))
  val budgetRepositoriesFetchError = Kamon.metrics.counter(s"GetAdvertiserIdRepository-fetch-error")

  def getConnectionWithAdRequestId(id: BidRequestId): T = {
    val head  = id.value.last
    val count = base.indexOf(head) % dbCount
    logger.debug(s"GetAdvertiserIdRepositoryBalancer: $count / $dbCount")
    if (count >= dbCount) {
      budgetRepositoriesFetchError.increment()
      dbs.head
    } else {
      budgetRepositories.apply(count).increment()
      dbs.apply(count)
    }
  }

  private val base = "0123456789abcdefghijklmnopqrstuvuxyz"

}
