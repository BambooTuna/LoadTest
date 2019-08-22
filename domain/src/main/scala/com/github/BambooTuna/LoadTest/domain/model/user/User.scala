package com.github.BambooTuna.LoadTest.domain.model.user

import com.github.BambooTuna.LoadTest.domain.aggregate.AggregateLongId

case class UserId(value: Long) extends AggregateLongId

case class User(userId: UserId, name: Name, age: Age)
