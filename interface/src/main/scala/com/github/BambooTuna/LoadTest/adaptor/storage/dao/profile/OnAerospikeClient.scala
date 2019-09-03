package com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile

import com.aerospike.client.async.AsyncClient
import ru.tinkoff.aerospikemacro.domain.DBCredentials

trait OnAerospikeClient extends Client {

  override type Connection = AsyncClient

  val profile: DBCredentials

}
