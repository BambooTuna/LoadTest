package com.github.BambooTuna.LoadTest.gatling.runner

import com.google.auth.oauth2.ServiceAccountCredentials

import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions

import com.google.cloud.storage.Bucket
import com.google.cloud.storage.BucketInfo

import java.io.FileInputStream

//https://gist.github.com/TakashiOshikawa/c1a1a144e856c3f0f3b98e88fc782e0f
case class GoogleCloudStorage(projectName: String, credentialFilePath: String) {
  require(projectName.nonEmpty && credentialFilePath.nonEmpty)

  val storage: Storage =
    StorageOptions
      .newBuilder()
      .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(credentialFilePath)))
      .setProjectId(projectName)
      .build()
      .getService

  def getBucket(bucketName: String): Option[Bucket] =
    Option(storage.get(bucketName))

  def createBucket(bucketName: String, location: String): Bucket = {
    getBucket(bucketName).getOrElse(
      storage.create(
        BucketInfo
          .newBuilder(bucketName)
          .setLocation(location)
          .build()
      )
    )
  }

}
