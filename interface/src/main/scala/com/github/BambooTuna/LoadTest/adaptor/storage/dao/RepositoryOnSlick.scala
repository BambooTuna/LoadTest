package com.github.BambooTuna.LoadTest.adaptor.storage.dao

import com.github.BambooTuna.LoadTest.domain.model.user.Name
import slick.jdbc.MySQLProfile.api._

import monix.eval.Task

trait RepositoryOnSlick[I] {

  val tableName: String
  val connection: Database

  def put(record: I): Task[Long]
  def putMulti(records: Seq[UserMessageRecord]): Task[Long]
  def get(id: Name): Task[Option[UserMessageRecord]]
  def getMulti(ids: Seq[Name]): Task[Seq[UserMessageRecord]]

  def delete(id: Name): Task[Long]
  def deleteMulti(ids: Seq[Name]): Task[Long]
  def softDelete(id: Name): Task[Long]
  def softDeleteMulti(ids: Seq[Name]): Task[Long]

}
