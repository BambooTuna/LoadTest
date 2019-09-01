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
