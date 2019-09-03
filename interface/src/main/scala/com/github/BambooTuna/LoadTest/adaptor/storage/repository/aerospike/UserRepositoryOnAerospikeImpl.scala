package com.github.BambooTuna.LoadTest.adaptor.storage.repository.aerospike

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.aerospike.UserComponentOnAerospike
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnAerospikeClient
import monix.eval.Task

class UserRepositoryOnAerospikeImpl(client: OnAerospikeClient) extends UserRepositoryOnAerospike with UserComponentOnAerospike {

  override def get(id: Id): Task[Option[Record]] = ???

  override def getMulti(ids: Seq[Id]): Task[Seq[Record]] = ???

  override def put(record: Record): Task[Long] = ???

  override def putMulti(records: Seq[Record]): Task[Long] = ???

  override def delete(id: Id): Task[Long] = ???

  override def deleteMulti(ids: Seq[Id]): Task[Long] = ???

  override def softDelete(id: Id): Task[Long] = ???

  override def softDeleteMulti(ids: Seq[Id]): Task[Long] = ???

}
