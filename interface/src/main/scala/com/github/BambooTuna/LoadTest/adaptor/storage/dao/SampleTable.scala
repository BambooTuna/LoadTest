package com.github.BambooTuna.LoadTest.adaptor.storage.dao

import slick.jdbc.MySQLProfile.api._

case class SampleTableColumn(id: Int, text: String)
case class SampleTable(tag: Tag) extends Table[SampleTableColumn](tag, "sample_table") {

  def id   = column[Int]("id", O.PrimaryKey)
  def text = column[String]("t")
  def *    = (id, text) <> (SampleTableColumn.tupled, SampleTableColumn.unapply)

}

object SampleTable {
  lazy val addresses = TableQuery[SampleTable]
}
