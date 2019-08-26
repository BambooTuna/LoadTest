package com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile

import slick.jdbc.JdbcProfile

trait SlickProfile {

  val profile: JdbcProfile

  val db: JdbcProfile#Backend#Database

}
