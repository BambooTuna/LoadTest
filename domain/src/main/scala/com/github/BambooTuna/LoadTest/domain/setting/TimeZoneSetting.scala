package com.github.BambooTuna.LoadTest.domain.setting

import java.time.ZoneId
import scala.concurrent.duration._

object TimeZoneSetting {

  val zone = ZoneId.of("Asia/Tokyo")

  //TODO
  val timeout = 80.millisecond

}
