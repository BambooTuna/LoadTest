package com.github.BambooTuna.LoadTest.adaptor.storage.dao

trait RepositorySupport[M[_], I, O] {

  type Id     = I
  type Record = O

  def resolveById(id: Id): M[Option[Record]]

  def insert(id: Id, record: Record): M[Long]

//  def get(id: Id): M[Option[Record]]
//  def getMulti(ids: Seq[Id]): M[Seq[Record]]
//
//  def put(record: Record): M[Long]
//  def putMulti(records: Seq[Record]): M[Long]
//
//  def delete(id: Id): M[Long]
//  def deleteMulti(ids: Seq[Id]): M[Long]
//
//  def softDelete(id: Id): M[Long]
//  def softDeleteMulti(ids: Seq[Id]): M[Long]

}
