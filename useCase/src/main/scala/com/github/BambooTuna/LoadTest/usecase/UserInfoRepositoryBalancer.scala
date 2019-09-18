package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.UserInfoDao
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.UserDeviceId
import kamon.Kamon
import org.slf4j.LoggerFactory

case class UserInfoRepositoryBalancer[T <: UserInfoDao](dbs: Seq[T]) {
  require(dbs.nonEmpty)

  val logger = LoggerFactory.getLogger(getClass)

  lazy val dbCount = dbs.size

  val userRepositories           = (1 to dbCount).map(i => Kamon.metrics.counter(s"UserInfoRepository-$i"))
  val userRepositoriesFetchError = Kamon.metrics.counter(s"UserInfoRepository-fetch-error")

  def getConnectionWithUserDeviceId(id: UserDeviceId): T = {
    val head  = id.value.last
    val count = base.indexOf(head) % dbCount
    logger.debug(s"UserInfoRepositoryBalancer: $count / $dbCount")
    if (count >= dbCount) {
      userRepositoriesFetchError.increment()
      dbs.head
    } else {
      userRepositories.apply(count).increment()
      dbs.apply(count)
    }
  }

  private val base = "0123456789abcdefghijklmnopqrstuvuxyz"

}
