package com.github.BambooTuna.LoadTest.boot.server

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.SlickSetting
import slick.jdbc
import slick.jdbc.MySQLProfile.api._

object SlickSettingImpl extends SlickSetting {

  override protected val connectionPool: jdbc.MySQLProfile.backend.Database = Database.forConfig("mysql")

  override val db: Database = connectionPool

}
