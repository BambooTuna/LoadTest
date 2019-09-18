//package com.github.BambooTuna.LoadTest.adaptor.storage.repository.aerospike
//
//import com.github.BambooTuna.LoadTest.adaptor.storage.dao.aerospike._
//import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnAerospikeClient
//import io.circe._
//import io.circe.generic.auto._
//import io.circe.syntax._
//import monix.eval.Task
//import ru.tinkoff.aerospikescala.domain.SingleBin
//
//class BudgetRepositoryOnAerospikeImpl(val client: OnAerospikeClient)
//    extends BudgetRepositoryOnAerospike
//    with BudgetComponentOnAerospike {
//
//  val db           = client.db
//  implicit val dbc = client.profile
//
//  override def get(id: Id): Task[Option[Record]] =
//    Task
//      .deferFutureAction { implicit ec =>
//        db.getString(generateKey(id)).map(
//            parser.decode[BudgetEventModelJson](_).map(v => (id, convertToRecord(v))).toOption
//          )
//      }
//
//  override def getMulti(ids: Seq[Id]): Task[Seq[Record]] = ???
//
//  override def put(record: Record): Task[Long] =
//    Task
//      .deferFutureAction { implicit ec =>
//        db.putString(generateKey(record._1), SingleBin("budget", record.asJson.noSpaces)).map(
//            _ => 1L
//          )
//      }
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
