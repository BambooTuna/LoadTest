package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.BudgetDao
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.AdvertiserId
import kamon.Kamon
import org.slf4j.LoggerFactory

case class BudgetRepositoryBalancer[+T <: BudgetDao](dbs: Seq[T]) {
  require(dbs.nonEmpty)

  val logger = LoggerFactory.getLogger(getClass)

  lazy val dbCount = dbs.size

  val budgetRepositories           = (1 to dbCount).map(i => Kamon.metrics.counter(s"BudgetRepository-$i"))
  val budgetRepositoriesFetchError = Kamon.metrics.counter(s"BudgetRepository-fetch-error")

  def getConnectionWithAdvertiserId(id: AdvertiserId): T = {
    val count = id.value % dbCount
    logger.debug(s"BudgetRepositoryBalancer: $count / $dbCount")
    if (count >= dbCount) {
      budgetRepositoriesFetchError.increment()
      dbs.head
    } else {
      budgetRepositories.apply(count).increment()
      dbs.apply(count)
    }
  }

}
