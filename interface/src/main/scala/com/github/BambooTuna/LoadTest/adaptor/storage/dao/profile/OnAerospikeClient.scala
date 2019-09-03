package com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile

import ru.tinkoff.aerospikeexamples.example.SampleScheme
import ru.tinkoff.aerospikemacro.domain.DBCredentials

trait OnAerospikeClient extends Client {

  override type Connection = SampleScheme

  val profile: DBCredentials

}
