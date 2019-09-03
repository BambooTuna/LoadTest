package com.github.BambooTuna.LoadTest.adaptor.storage.dao.aerospike

import com.aerospike.client.Host
import com.aerospike.client.async.{ AsyncClient, AsyncClientPolicy }
import com.github.BambooTuna.LoadTest.adaptor.storage.dao.profile.OnAerospikeClient
import ru.tinkoff.aerospike.dsl.SpikeImpl
import ru.tinkoff.aerospikeexamples.example.SampleScheme
import ru.tinkoff.aerospikemacro.domain.DBCredentials

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success, Try }

case class AerospikeSetting(namespace: String, setName: String, hosts: Seq[String], port: Int) {

  def client = new OnAerospikeClient {

    override val db = SampleScheme(
      new SpikeImpl(
        Try(new AsyncClient(new AsyncClientPolicy, hosts.map(new Host(_, port)): _*)) match {
          case Success(c)  => c
          case Failure(th) => throw th
        }
      )
    )
    override val profile: DBCredentials = DBCredentials(namespace, setName)

  }

}
