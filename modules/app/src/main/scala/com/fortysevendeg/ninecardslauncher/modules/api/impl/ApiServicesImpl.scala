package com.fortysevendeg.ninecardslauncher.modules.api.impl

import android.content.res.Resources
import com.fortysevendeg.ninecardslauncher.api.model.PackagesRequest
import com.fortysevendeg.ninecardslauncher.api.services.{ApiUserConfigService, ApiGooglePlayService, ApiUserService}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.di.Module
import com.fortysevendeg.ninecardslauncher.models.GooglePlaySimplePackages
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher2.R

import scala.concurrent.ExecutionContext

class ApiServicesImpl(
    resources: Resources,
    userService: ApiUserService,
    googlePlayService: ApiGooglePlayService,
    userConfigService: ApiUserConfigService)
    extends ApiServices
    with Module
    with Conversions {

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val HeaderAppId = "X-Appsly-Application-Id"

  val HeaderAppKey = "X-Appsly-REST-API-Key"

  val HeaderDevice = "X-Android-ID"

  val HeaderToken = "X-Appsly-Session-Token"

  val HeaderLocalization = "X-Android-Market-Localization"

  val baseHeader: Seq[(String, String)] = Seq(
    (HeaderAppId, getString(R.string.api_app_id)),
    (HeaderAppKey, getString(R.string.api_app_key)),
    (HeaderLocalization, getString(R.string.api_localization)))

  import com.fortysevendeg.ninecardslauncher.api.reads.UserConfigImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.GooglePlayImplicits._
  
  override def login: Service[LoginRequest, LoginResponse] =
    request =>
      for {
        response <- userService.login(fromLoginRequest(request), baseHeader)
      } yield LoginResponse(response.statusCode, response.data map toUser)

  override def linkGoogleAccount: Service[LinkGoogleAccountRequest, LoginResponse] =
    request =>
      for {
        response <- userService.linkAuthData(fromLinkGoogleAccountRequest(request), createHeader(request.deviceId, request.token))
      } yield LoginResponse(response.statusCode, response.data map toUser)

  override def createInstallation: Service[InstallationRequest, InstallationResponse] =
    request =>
      for {
        response <- userService.createInstallation(fromInstallationRequest(request), baseHeader)
      } yield InstallationResponse(response.statusCode, response.data map toInstallation)

  override def updateInstallation: Service[InstallationRequest, UpdateInstallationResponse] =
    request =>
      for {
        response <- userService.updateInstallation(fromInstallationRequest(request), baseHeader)
      } yield UpdateInstallationResponse(response.statusCode)

  override def googlePlayPackage: Service[GooglePlayPackageRequest, GooglePlayPackageResponse] =
    request =>
      for {
        response <- googlePlayService.getGooglePlayPackage(request.packageName, createHeader(request.deviceId, request.token))
      } yield GooglePlayPackageResponse(response.statusCode, response.data map (playApp => toGooglePlayApp(playApp.docV2)))

  override def googlePlayPackages: Service[GooglePlayPackagesRequest, GooglePlayPackagesResponse] =
    request =>
      for {
        response <- googlePlayService.getGooglePlayPackages(PackagesRequest(request.packageNames), createHeader(request.deviceId, request.token))
      } yield GooglePlayPackagesResponse(response.statusCode, response.data map (packages => toGooglePlayPackageSeq(packages.items)) getOrElse Seq.empty)

  override def googlePlaySimplePackages: Service[GooglePlaySimplePackagesRequest, GooglePlaySimplePackagesResponse] =
    request =>
      for {
        response <- googlePlayService.getGooglePlaySimplePackages(PackagesRequest(request.items), createHeader(request.deviceId, request.token))
      } yield GooglePlaySimplePackagesResponse(
        response.statusCode,
        response.data.map(playApp => toGooglePlaySimplePackages(playApp)).getOrElse(GooglePlaySimplePackages(Seq.empty, Seq.empty)))

  override def getUserConfig: Service[GetUserConfigRequest, GetUserConfigResponse] =
    request =>
      for {
        response <- userConfigService.getUserConfig(createHeader(request.deviceId, request.token))
      } yield GetUserConfigResponse(response.statusCode, response.data map toUserConfig)

  override def saveDevice: Service[SaveDeviceRequest, SaveDeviceResponse] =
    request =>
      for {
        response <- userConfigService.saveDevice(
          fromUserConfigDevice(request.userConfigDevice),
          createHeader(request.deviceId, request.token))
      } yield SaveDeviceResponse(response.statusCode, response.data map toUserConfig)

  override def saveGeoInfo: Service[SaveGeoInfoRequest, SaveGeoInfoResponse] =
    request =>
      for {
        response <- userConfigService.saveGeoInfo(
          fromUserConfigGeoInfo(request.userConfigGeoInfo),
          createHeader(request.deviceId, request.token))
      } yield SaveGeoInfoResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointPurchaseProduct: Service[CheckpointPurchaseProductRequest, CheckpointPurchaseProductResponse] =
    request =>
      for {
        response <- userConfigService.checkpointPurchaseProduct(
          request.productId,
          createHeader(request.deviceId, request.token))
      } yield CheckpointPurchaseProductResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointCustomCollection: Service[CheckpointCustomCollectionRequest, CheckpointCustomCollectionResponse] =
    request =>
      for {
        response <- userConfigService.checkpointCustomCollection(
          createHeader(request.deviceId, request.token))
      } yield CheckpointCustomCollectionResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointJoinedBy: Service[CheckpointJoinedByRequest, CheckpointJoinedByResponse] =
    request =>
      for {
        response <- userConfigService.checkpointJoinedBy(
          request.otherConfigId,
          createHeader(request.deviceId, request.token))
      } yield CheckpointJoinedByResponse(response.statusCode, response.data map toUserConfig)

  override def tester: Service[TesterRequest, TesterResponse] =
    request =>
      for {
        response <- userConfigService.tester(
          request.replace,
          createHeader(request.deviceId, request.token))
      } yield TesterResponse(response.statusCode, response.data map toUserConfig)

  private def getString(string: Int) = resources.getString(string)

  private def createHeader(device: String, token: String) =
    baseHeader :+ (HeaderDevice, device) :+ (HeaderToken, token)

}