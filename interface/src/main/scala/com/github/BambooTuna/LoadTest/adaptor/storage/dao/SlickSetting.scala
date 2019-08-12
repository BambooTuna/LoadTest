package com.github.BambooTuna.LoadTest.adaptor.storage.dao

import slick.jdbc
import slick.jdbc.MySQLProfile.api._

trait SlickSetting {

  protected val connectionPool: jdbc.MySQLProfile.backend.Database

  val db: Database

}
