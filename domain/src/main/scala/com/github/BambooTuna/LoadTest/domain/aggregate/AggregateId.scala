package com.github.BambooTuna.LoadTest.domain.aggregate

trait AggregateId {
  type IdType
  val value: IdType
}
