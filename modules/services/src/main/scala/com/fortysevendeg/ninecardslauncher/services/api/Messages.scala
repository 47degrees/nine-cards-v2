package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.services.api.models._

case class RequestConfigV1(deviceId: String, token: String, marketToken: Option[String])

case class RequestConfig(
  apiKey: String,
  sessionToken: String,
  androidId: String,
  marketToken: Option[String] = None)

case class LoginResponse(
  statusCode: Int,
  apiKey: String,
  sessionToken: String)

case class LoginResponseV1(
  statusCode: Int,
  userId: Option[String],
  sessionToken: Option[String],
  email: Option[String],
  devices: Seq[LoginV1Device])

case class UpdateInstallationResponse(
  statusCode: Int)

case class GooglePlayPackageResponse(
  statusCode: Int,
  app: CategorizedPackage)

case class CategorizedPackage(
  packageName: String,
  category: Option[String])

case class CategorizedDetailPackage(
  packageName: String,
  title: String,
  category: Option[String],
  icon: String,
  free: Boolean,
  downloads: String,
  stars: Double)

case class GooglePlayPackagesResponse(
  statusCode: Int,
  packages: Seq[CategorizedPackage])

case class GooglePlayPackagesDetailResponse(
  statusCode: Int,
  packages: Seq[CategorizedDetailPackage])

trait UserV1Response {
  def statusCode: Int

  def userConfig: UserV1
}

case class GetUserV1Response(
  statusCode: Int,
  userConfig: UserV1) extends UserV1Response

case class RecommendationResponse(
  statusCode: Int,
  seq: Seq[RecommendationApp])

case class RecommendationApp(
  packageName: String,
  name: String,
  downloads: String,
  icon: String,
  stars: Double,
  free: Boolean,
  description: String,
  screenshots: Seq[String])

case class SharedCollectionResponseList(
  statusCode: Int,
  items: Seq[SharedCollection])

case class SharedCollectionResponse(
  statusCode: Int,
  sharedCollection: SharedCollection)

case class SharedCollection(
  id: String,
  sharedCollectionId: String,
  publishedOn: Long,
  description: String,
  author: String,
  name: String,
  packages: Seq[String],
  resolvedPackages: Seq[SharedCollectionPackageResponse],
  views: Int,
  subscriptions: Option[Int],
  category: String,
  icon: String,
  community: Boolean)

case class SharedCollectionPackageResponse(
  packageName: String,
  title: String,
  icon: String,
  stars: Double,
  downloads: String,
  free: Boolean)

case class SubscriptionResponseList(
  statusCode: Int,
  items: Seq[SubscriptionResponse])

case class SubscriptionResponse(
  sharedCollectionId: String)

case class SubscribeResponse(statusCode: Int)

case class UnsubscribeResponse(statusCode: Int)

case class CreateSharedCollectionResponse(
  statusCode: Int,
  sharedCollectionId: String)

case class UpdateSharedCollectionResponse(
  statusCode: Int,
  sharedCollectionId: String)
