package com.github.BambooTuna.LoadTest.adaptor.storage.repository.jdbc

import com.github.BambooTuna.LoadTest.adaptor.storage.dao.jdbc.DaoOnSlick
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnSlickClient
import com.github.BambooTuna.LoadTest.domain.model.dsp.UserInfo
import com.github.BambooTuna.LoadTest.domain.model.dsp.ad.{ AdvertiserId, UserDeviceId }
import com.github.BambooTuna.LoadTest.domain.model.dsp.user.GameInstallCount
import monix.eval.Task

class UserInfoRepositoryOnJDBCImpl(val client: OnSlickClient) extends UserInfoRepositoryOnJDBC with DaoOnSlick {

  val tableName = "user"

  import client.profile.api._

  case class UserRecord(
      userId: String,
      advertiser_id: Int,
      game_install_count: Int
  )

  case class Users(tag: Tag) extends Table[UserRecord](tag, tableName) {
    def userId             = column[String]("user_id", O.PrimaryKey) // 主キー
    def advertiser_id      = column[Int]("advertiser_id")
    def game_install_count = column[Int]("game_install_count")
    def * =
      (
        userId,
        advertiser_id,
        game_install_count
      ) <> (UserRecord.tupled, UserRecord.unapply)
  }

  object UserDao extends TableQuery(Users)
  val dao: UserDao.type = UserDao

  private def convertToRecord(id: UserDeviceId, item: UserInfo): UserRecord =
    UserRecord(
      id.value,
      item.advertiserId.value,
      item.gameInstallCount.value.toInt
    )

  private def convertToAggregate(item: UserRecord): UserInfo =
    UserInfo(
      UserDeviceId(item.userId),
      AdvertiserId(item.advertiser_id),
      GameInstallCount(item.game_install_count)
    )

  private val resolveByIdCompiled = Compiled((id: Rep[String]) => dao.filter(_.userId === id))
  def resolveById(id: Id): Task[Option[Record]] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db.run(resolveByIdCompiled(id.value).result)
      }.map(_.headOption.map(convertToAggregate))
  }

  val insertCompiled = Compiled(dao.filter(_ => true: Rep[Boolean]))
  def insert(id: Id, record: Record): Task[Long] = {
    Task
      .deferFutureAction { implicit ec =>
        client.db.run(insertCompiled += convertToRecord(id, record))
      }.map(_.toLong)
  }

}
