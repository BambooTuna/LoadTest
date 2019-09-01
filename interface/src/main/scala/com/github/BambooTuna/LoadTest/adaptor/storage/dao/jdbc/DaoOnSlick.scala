package com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc

import java.time.{ Instant, ZoneId, ZonedDateTime }

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnSlickClient

trait DaoOnSlick {

  protected val tableName: String
  protected val client: OnSlickClient

  import client.profile.api._

  implicit val zonedDateTimeColumnType =
    MappedColumnType.base[ZonedDateTime, java.sql.Timestamp](
      { zdt =>
        new java.sql.Timestamp(zdt.toInstant.toEpochMilli)
      }, { ts =>
        val instant = Instant.ofEpochMilli(ts.getTime)
        ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
      }
    )

}
