package com.github.BambooTuna.LoadTest.gatling.runner

import com.google.auth.oauth2.ServiceAccountCredentials

import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions

import java.nio.charset.StandardCharsets.UTF_8
import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
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

  def createBucket(bucketName: String, location: String): Bucket =
    storage.create(
      BucketInfo
        .newBuilder(bucketName)
        .setLocation(location)
        .build()
    )

  def createTestBlob(bucketName: String, filePath: String): Blob = {
    val blobId: BlobId     = BlobId.of(bucketName, filePath)
    val blobInfo: BlobInfo = BlobInfo.newBuilder(blobId).build()
    storage.create(blobInfo, "a simple blob".getBytes(UTF_8))
  }

}
