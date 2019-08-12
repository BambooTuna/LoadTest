package com.github.BambooTuna.LoadTest.adaptor.storage.dao

import com.github.BambooTuna.LoadTest.domain.model.user.{ Age, Name }
import slick.jdbc.MySQLProfile.api._
import monix.eval.Task

import scala.concurrent.Future

case class UserMessageRecord(name: Name, age: Age)

trait DspRepositoryOnSlick {

  val tableName: String
  val connection: Database

  def put(record: UserMessageRecord): Task[Long]
  def putMulti(records: Seq[UserMessageRecord]): Task[Long]
  def get(id: Name): Task[Option[UserMessageRecord]]
  def getMulti(ids: Seq[Name]): Task[Seq[UserMessageRecord]]

  def delete(id: Name): Task[Long]
  def deleteMulti(ids: Seq[Name]): Task[Long]
  def softDelete(id: Name): Task[Long]
  def softDeleteMulti(ids: Seq[Name]): Task[Long]

  def getAll[I, O <: Table[I]](table: TableQuery[O]): Future[Seq[I]] = {
    connection.run(table.result)
  }

  def getWithFilter[I, O <: Table[I]](f: O => Rep[Boolean], table: TableQuery[O]): Future[Seq[I]] = {
    connection.run(table.filter(f(_)).result)
  }

  def insert[I, O <: Table[I]](insertData: I, table: TableQuery[O]): Future[Int] = {
    connection.run(table += insertData)
  }

}
