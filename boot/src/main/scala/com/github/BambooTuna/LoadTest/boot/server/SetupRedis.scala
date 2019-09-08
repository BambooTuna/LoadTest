package com.github.BambooTuna.LoadTest.boot.server

import java.io._

import com.github.BambooTuna.LoadTest.usecase.LoadTestProtocol.AddUserCommandRequest
import com.github.BambooTuna.LoadTest.usecase.json.UserDataJson

import scala.util.Try
import java.net.{HttpURLConnection, URL}

import sys.process._
import java.net.URL
import java.io.File

import akka.NotUsed
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}
import com.github.BambooTuna.LoadTest.usecase.AddUserUseCase

object SetupRedis {

  def tryProcessSource(
      file: File,
      parseLine: (Int, String) => Option[List[String]] = (index, unparsedLine) => Some(List(unparsedLine)),
      filterLine: (Int, List[String]) => Option[Boolean] = (index, parsedValues) => Some(true),
      retainValues: (Int, List[String]) => Option[List[String]] = (index, parsedValues) => Some(parsedValues)
  ): Try[List[List[String]]] = {
    def usingSource[S <: scala.io.Source, R](source: S)(transfer: S => R): Try[R] =
      try { Try(transfer(source)) } finally { source.close() }
    def recursive(
        remaining: Iterator[(String, Int)],
        accumulator: List[List[String]],
        isEarlyAbort: Boolean = false
    ): List[List[String]] = {
      if (isEarlyAbort || !remaining.hasNext)
        accumulator
      else {
        val (line, index) =
          remaining.next
        parseLine(index, line) match {
          case Some(values) =>
            filterLine(index, values) match {
              case Some(keep) =>
                if (keep)
                  retainValues(index, values) match {
                    case Some(valuesNew) =>
                      recursive(remaining, valuesNew :: accumulator) //capture values
                    case None =>
                      recursive(remaining, accumulator, isEarlyAbort = true) //early abort
                  } else
                  recursive(remaining, accumulator) //discard row
              case None =>
                recursive(remaining, accumulator, isEarlyAbort = true) //early abort
            }
          case None =>
            recursive(remaining, accumulator, isEarlyAbort = true) //early abort
        }
      }
    }
    Try(scala.io.Source.fromFile(file)).flatMap(
      bufferedSource =>
        usingSource(bufferedSource) { source =>
          recursive(source.getLines().buffered.zipWithIndex, Nil).reverse
      }
    )
  }

  def fileDownloader(url: String, filename: String) = {
    new URL(url) #> new File(filename) !!
  }

  def downloadFile(link: String, saveFileName: String) {

    val url        = new URL(link)
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("GET")
    val in: InputStream   = connection.getInputStream
    val fileToDownloadAs  = new java.io.File(saveFileName)
    val out: OutputStream = new BufferedOutputStream(new FileOutputStream(fileToDownloadAs))
    val byteArray         = Stream.continually(in.read).takeWhile(-1 !=).map(_.toByte).toArray
    out.write(byteArray)

  }

}
