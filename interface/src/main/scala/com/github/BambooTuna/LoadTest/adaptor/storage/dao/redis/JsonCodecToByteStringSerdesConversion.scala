package com.github.BambooTuna.LoadTest.adaptor.storage.dao.redis

import akka.util.ByteString
import io.circe.parser.decode
import io.circe.{ Decoder, Encoder }
import redis.{ ByteStringDeserializer, ByteStringSerializer }

object JsonCodecToByteStringSerdesConversion {

  implicit def encoderToByteStringSerializer[T](implicit ec: Encoder[T]): ByteStringSerializer[T] =
    (data: T) => ByteString(ec.apply(data).noSpaces)

  implicit def decoderToByteStringSerializer[T](implicit dc: Decoder[T]): ByteStringDeserializer[T] =
    (bs: ByteString) => decode(bs.utf8String)(dc).right.get

}

object StringByteStringSerializer {

  implicit object String extends ByteStringSerializer[String] {
    def serialize(key: String): ByteString = ByteString(key)
  }

}

object StringByteStringDeserializer {

  implicit object String extends ByteStringDeserializer[String] {
    def deserialize(bs: ByteString): String = bs.utf8String
  }

}

object IntByteStringSerializer {

  implicit object IntConverter extends ByteStringSerializer[Int] {
    def serialize(i: Int): ByteString = ByteString(i.toString)
  }

}

object IntByteStringDeserializer {

  implicit object Int extends ByteStringDeserializer[Int] {
    def deserialize(bs: ByteString): Int = bs.utf8String.toInt
  }

}
