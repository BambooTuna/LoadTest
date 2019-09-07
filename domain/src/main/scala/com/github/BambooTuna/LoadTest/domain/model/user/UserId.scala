package com.github.BambooTuna.LoadTest.domain.model.user

import com.github.BambooTuna.LoadTest.domain.aggregate.AggregateStringId

case class UserId(value: String) extends AggregateStringId
