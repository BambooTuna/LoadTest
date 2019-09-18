//package com.github.BambooTuna.LoadTest.adaptor.storage.repository.aerospike
//
//import com.github.BambooTuna.LoadTest.adaptor.storage.dao.aerospike.UserComponentOnAerospike
//import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnAerospikeClient
//import com.github.BambooTuna.LoadTest.usecase.json.UserInfoJson
//import monix.eval.Task
//import ru.tinkoff.aerospikescala.domain.SingleBin
//import io.circe._
//import io.circe.syntax._
//import io.circe.generic.auto._
//
//class UserInfoRepositoryOnAerospikeImpl(val client: OnAerospikeClient)
//    extends UserInfoRepositoryOnAerospike
//    with UserComponentOnAerospike {
//
//  val db           = client.db
//  implicit val dbc = client.profile
//
//  override def get(id: Id): Task[Option[Record]] =
//    Task
//      .deferFutureAction { implicit ec =>
//        db.getString(generateKey(id)).map(parser.decode[UserInfoJson](_).map((id, _)).toOption)
//      }
//
//  override def getMulti(ids: Seq[Id]): Task[Seq[Record]] = ???
//
//  override def put(record: Record): Task[Long] =
//    Task
//      .deferFutureAction { implicit ec =>
//        db.putString(generateKey(record._1), SingleBin("user_id", record.asJson.noSpaces)).map(
//            _ => 1L
//          )
//      }
//
//}
