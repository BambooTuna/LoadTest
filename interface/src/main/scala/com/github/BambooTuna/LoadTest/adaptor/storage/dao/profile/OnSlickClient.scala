package com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile

import slick.jdbc.JdbcProfile

trait OnSlickClient extends Client {

  override type Connection = JdbcProfile#Backend#Database

  val profile: JdbcProfile

}
