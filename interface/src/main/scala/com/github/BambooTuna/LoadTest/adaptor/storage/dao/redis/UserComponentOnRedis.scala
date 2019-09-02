package com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis

import com.github.BambooTuna.LoadTest.domain.model.user.{ Age, Name, User, UserId }

trait UserComponentOnRedis {

  protected case class RecordJson(key: String, name: String, age: Int)

  protected def convertToJson(data: User): RecordJson =
    RecordJson(
      key = data.userId.value.toString,
      name = data.name.value,
      age = data.age.value
    )

  protected def convertToData(json: RecordJson): User =
    User(
      userId = UserId(value = json.key.toLong),
      name = Name(value = json.name),
      age = Age(value = json.age)
    )

  protected def generateKey(id: UserId): String = s"user_${id.value.toString}"

}
