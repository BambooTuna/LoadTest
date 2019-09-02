package com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile

trait Client {

  type Connection

  val db: Connection

}
