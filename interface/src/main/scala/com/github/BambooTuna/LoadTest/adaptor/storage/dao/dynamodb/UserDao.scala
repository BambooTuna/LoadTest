package com.github.BambooTuna.LoadTest.adaptor.storage.dao.dynamodb

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.DaoOnSlick
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.SlickProfile
import com.github.BambooTuna.LoadTest.domain.model.user.{ User, UserId }
import monix.eval.Task

import com.github.j5ik2o.dddbase.slick.SlickDaoSupport
//class UserDao(val client: SlickProfile) extends DaoOnSlick[Task, UserId, User] {
//
//  val tableName = "loadtest-user"
//
//  override def get(id: Id): Task[Option[Record]] = ???
//
//  override def getMulti(ids: Seq[Id]): Task[Seq[Record]] = ???
//
//  override def put(record: Record): Task[Long] = ???
//
//  override def putMulti(records: Seq[Record]): Task[Long] = ???
//
//  override def delete(id: Id): Task[Long] = ???
//
//  override def deleteMulti(ids: Seq[Id]): Task[Long] = ???
//
//  override def softDelete(id: Id): Task[Long] = ???
//
//  override def softDeleteMulti(ids: Seq[Id]): Task[Long] = ???
//
//}
