package com.github.BambooTuna.LoadTest.usecase

import com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc.UserRepositoryOnJDBC
import monix.eval.Task

class UserRepositoryOnJDBCMock extends UserRepositoryOnJDBC {

  override def get(id: Id): Task[Option[Record]] = ???

  override def getMulti(ids: Seq[Id]): Task[Seq[Record]] = ???

  override def put(record: Record): Task[Long] = Task.pure(1L)

  override def putMulti(records: Seq[Record]): Task[Long] = ???

  override def delete(id: Id): Task[Long] = ???

  override def deleteMulti(ids: Seq[Id]): Task[Long] = ???

  override def softDelete(id: Id): Task[Long] = ???

  override def softDeleteMulti(ids: Seq[Id]): Task[Long] = ???

}
