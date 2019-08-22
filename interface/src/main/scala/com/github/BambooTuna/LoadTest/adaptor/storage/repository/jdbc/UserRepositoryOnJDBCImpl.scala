package com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc.UserComponent
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.SlickProfile
import monix.eval.Task

class UserRepositoryOnJDBCImpl(val client: SlickProfile) extends UserRepositoryOnJDBC with UserComponent {

  import client.profile.api._

  val dao: UserDao.type = UserDao

  override def get(id: Id): Task[Option[Record]] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db.run(dao.filter(_.userId === id.value).result)
      }.map(_.headOption.map(convertToAggregate))
  }

  override def getMulti(ids: Seq[Id]): Task[Seq[Record]] = ???

  override def put(record: Record): Task[Long] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db.run(dao += convertToRecord(record))
      }.map(_.toLong)
  }

  override def putMulti(records: Seq[Record]): Task[Long] = ???

  override def delete(id: Id): Task[Long] = ???

  override def deleteMulti(ids: Seq[Id]): Task[Long] = ???

  override def softDelete(id: Id): Task[Long] = ???

  override def softDeleteMulti(ids: Seq[Id]): Task[Long] = ???

}
