package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.UserRepository
import com.github.BambooTuna.LoadTest.domain.model.user.UserId
import kamon.Kamon
import org.slf4j.LoggerFactory

case class GetUserRepositoryBalance[T <: UserRepository](dbs: Seq[T]) {
  require(dbs.nonEmpty)

  val logger = LoggerFactory.getLogger(getClass)

  lazy val dbCount = dbs.size

  val userRepositories           = (1 to dbCount).map(i => Kamon.metrics.counter(s"UserRepository-$i"))
  val userRepositoriesFetchError = Kamon.metrics.counter(s"UserRepository-fetch-error")

  def getConnectionWithUserId(id: UserId): T = {
    val head  = id.value.last
    val count = base.indexOf(head) % dbCount
    logger.debug(s"GetUserRepositoryBalance: $count / $dbCount")
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
