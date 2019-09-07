package com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc

import com.github.BambooTuna.LoadTest.domain.model.user._
import com.github.BambooTuna.LoadTest.usecase.json.UserDataJson
import slick.jdbc.MySQLProfile.api._

trait UserComponentOnJDBC extends DaoOnSlick {

  val tableName = "user"

  case class UserRecord(
      userId: String,
      advertiser_id: Int,
      game_install_count: Int,
      game_login_count: Int,
      game_paid_count: Int,
      game_tutorial_count: Int,
      game_extension_count: Int
  )

  case class Users(tag: Tag) extends Table[UserRecord](tag, tableName) {
    def userId               = column[String]("user_id", O.PrimaryKey) // 主キー
    def advertiser_id        = column[Int]("advertiser_id")
    def game_install_count   = column[Int]("game_install_count")
    def game_login_count     = column[Int]("game_login_count")
    def game_paid_count      = column[Int]("game_paid_count")
    def game_tutorial_count  = column[Int]("game_tutorial_count")
    def game_extension_count = column[Int]("game_extension_count")
    def * =
      (
        userId,
        advertiser_id,
        game_install_count,
        game_login_count,
        game_paid_count,
        game_tutorial_count,
        game_extension_count
      ) <> (UserRecord.tupled, UserRecord.unapply)
  }

  object UserDao extends TableQuery(Users)

  def convertToRecord(id: UserId, item: UserDataJson): UserRecord =
    UserRecord(
      id.value,
      item.advertiser_id,
      item.game_install_count,
      item.game_login_count,
      item.game_paid_count,
      item.game_tutorial_count,
      item.game_extension_count
    )

  def convertToAggregate(item: UserRecord): UserDataJson =
    UserDataJson(
      item.userId,
      item.advertiser_id,
      item.game_install_count,
      item.game_login_count,
      item.game_paid_count,
      item.game_tutorial_count,
      item.game_extension_count
    )

}
