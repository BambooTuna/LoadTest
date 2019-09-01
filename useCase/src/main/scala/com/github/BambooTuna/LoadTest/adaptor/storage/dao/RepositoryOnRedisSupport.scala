package com.github.BambooTuna.LoadTest.adaptor.storage.dao

trait RepositoryOnRedisSupport[M[_], I, O] {

  type Id     = I
  type Record = O

  def find(id: Id): M[Option[Record]]
  def findMulti(ids: Seq[Id]): M[Seq[Record]]

  def store(record: (Id, Record)): M[Long]
  def storeMulti(records: Seq[(Id, Record)]): M[Long]

  def delete(id: Id): M[Long]
  def deleteMulti(ids: Seq[Id]): M[Long]

}
