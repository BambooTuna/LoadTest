package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.AdIdRepository
import com.github.BambooTuna.LoadTest.domain.model.ad.AdRequestId
import kamon.Kamon
import org.slf4j.LoggerFactory

case class GetAdIdRepositoryBalance[T <: AdIdRepository](dbs: Seq[T]) {
  require(dbs.nonEmpty)

  val logger = LoggerFactory.getLogger(getClass)

  lazy val dbCount = dbs.size

  val budgetRepositories           = (1 to dbCount).map(i => Kamon.metrics.counter(s"GetAdIdRepository-$i"))
  val budgetRepositoriesFetchError = Kamon.metrics.counter(s"GetAdIdRepository-fetch-error")

  def getConnectionWithAdRequestId(id: AdRequestId): T = {
    val head  = id.value.head
    val count = base.indexOf(head) % dbCount
    logger.debug(s"GetAdIdRepositoryBalance: $count / $dbCount")
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
