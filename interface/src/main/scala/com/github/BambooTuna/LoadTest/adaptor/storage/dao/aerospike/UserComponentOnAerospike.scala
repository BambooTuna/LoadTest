package com.github.BambooTuna.LoadTest.adaptor.storage.dao.aerospike

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnAerospikeClient
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.UserDeviceId

trait UserComponentOnAerospike {

  val client: OnAerospikeClient

  protected def generateKey(id: UserDeviceId): String = s"user_${id.value.toString}"

}
