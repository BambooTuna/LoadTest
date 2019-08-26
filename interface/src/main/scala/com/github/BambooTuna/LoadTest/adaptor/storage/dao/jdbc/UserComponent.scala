package com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc

import java.time.ZonedDateTime

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.DaoOnSlick
import com.github.BambooTuna.LoadTest.domain.model.user.{ Age, Name, User, UserId }
import slick.jdbc.MySQLProfile.api._

trait UserComponent extends DaoOnSlick {

  val tableName = "loadtest-user"

  case class UserRecord(
      userId: Long,
      name: String,
      age: Int,
      createdAt: java.time.ZonedDateTime,
      updatedAt: java.time.ZonedDateTime
  )

  case class Users(tag: Tag) extends Table[UserRecord](tag, "user") {
    def userId    = column[Long]("user_id", O.PrimaryKey) // 主キー
    def name      = column[String]("user_name")
    def age       = column[Int]("user_age")
    def createdAt = column[java.time.ZonedDateTime]("created_at")
    def updatedAt = column[java.time.ZonedDateTime]("updated_at")
    def *         = (userId, name, age, createdAt, updatedAt) <> (UserRecord.tupled, UserRecord.unapply)
  }

  object UserDao extends TableQuery(Users)

  def convertToRecord(item: User): UserRecord =
    UserRecord(item.userId.value, item.name.value, item.age.value, ZonedDateTime.now(), ZonedDateTime.now())

  def convertToAggregate(item: UserRecord): User =
    User(
      UserId(item.userId),
      Name(item.name),
      Age(item.age)
    )

}
