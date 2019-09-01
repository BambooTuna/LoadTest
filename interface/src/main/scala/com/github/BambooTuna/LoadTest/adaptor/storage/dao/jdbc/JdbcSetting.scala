package com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnSlickClient
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

case class JdbcSetting(config: DatabaseConfig[JdbcProfile]) {

  def client = new OnSlickClient {
    override val profile = config.profile
    override val db      = config.db
  }

}
