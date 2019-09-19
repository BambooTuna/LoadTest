package com.github.BambooTuna.LoadTest.domain.model.dsp.ad

import com.github.BambooTuna.LoadTest.domain.aggregate.AggregateStringId

case class UserDeviceId(value: String) extends AggregateStringId {

  def isEmpty: Boolean = value.isEmpty

  def nonEmpty: Boolean = value.nonEmpty

}

object UserDeviceId {

  def empty: UserDeviceId = UserDeviceId("")

}
