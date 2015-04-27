package com.fortysevendeg.ninecardslauncher.api.integration

import org.mockserver.integration.ClientAndServer._
import org.mockserver.logging.Logging
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest._
import org.mockserver.model.HttpResponse._
import org.specs2.specification.BeforeAfterEach

trait MockServerService
    extends BeforeAfterEach {

  val userConfigIdFirst = "12345678"
  val productId = "987654321"
  val joinedById = "13579"
  val testerValues = Map("key1" -> "value1", "key2" -> "value2")
  val packageName1 = "com.fortysevendeg.scala.android"
  val packageName2 = "com.fortysevendeg.android.scaladays"
  val searchQuery = "scala"
  val searchOffset = 0
  val searchLimit = 10

  val sharedCollectionIdFirst = "12345678"
  val sharedCollectionIdLast = "87654321"
  val sharedCollectionSize = 2
  val sharedCollectionType = "collectionType"
  val sharedCollectionCategory = "collectionCategory"
  val sharedCollectionKeywords = "keyword1,keyword2"

  val userConfigPathPrefix = "/ninecards/userconfig"
  val sharedCollectionPathPrefix = "/ninecards/collections"
  val googlePlayPathPrefix = "/googleplay/package"
  val googlePlayPackagesPathPrefix = "/googleplay/packages/detailed"
  val googlePlaySearchPathPrefix = "/googleplay/search"
  val googlePlaySimplePackagesPathPrefix = "/googleplay/packages/simple"
  val recommendationSponsoredPathPrefix = "/ninecards/collections/items/sponsored"
  val recommendationsPathPrefix = "/collections"
  val regexpPath = "[a-zA-Z0-9,\\.\\/]*"
  val jsonHeader = new Header("Content-Type", "application/json; charset=utf-8")
  val userConfigJson = "userConfig.json"
  val sharedCollectionJsonSingle = "sharedCollection.json"
  val sharedCollectionJsonList = "sharedCollectionList.json"
  val sharedCollectionJsonSubscription = "sharedCollectionSubscription.json"
  val googlePlayPackageJsonSingle = "googlePlayPackage.json"
  val googlePlayPackageJsonList = "googlePlayPackageList.json"
  val googlePlaySimplePackageJsonList = "googlePlaySimplePackageList.json"
  val googlePlaySearchJson = "googlePlaySearch.json"
  // TODO
  val recommendedCollectionsJson = ""
  val recommendedCollectionAppsJson = ""
  val recommendedAppsJson = "recommendationApps.json"
  val recommendationSponsoredJson = "recommendationSponsored.json"

  lazy val mockServer = startClientAndServer(9999)

  def beforeAll = {
    Logging.overrideLogLevel("ERROR")
    mockServer
  }

  def loadJson(file: String): String =
    scala.io.Source.fromInputStream(getClass.getResourceAsStream(s"/$file"), "UTF-8").mkString

  def afterAll = {
    mockServer.stop()
  }

}

trait UserConfigServer {

  self: MockServerService =>

  Logging.overrideLogLevel("ERROR")

  mockServer.when(
    request()
        .withMethod("GET")
        .withPath(s"$userConfigPathPrefix"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(userConfigJson)))

  mockServer.when(
    request()
        .withMethod("PUT")
        .withPath(s"$userConfigPathPrefix/device"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(userConfigJson)))

  mockServer.when(
    request()
        .withMethod("PUT")
        .withPath(s"$userConfigPathPrefix/geoInfo"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(userConfigJson)))

  mockServer.when(
    request()
        .withMethod("PUT")
        .withPath(s"$userConfigPathPrefix/checkpoint/purchase/$productId"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(userConfigJson)))

  mockServer.when(
    request()
        .withMethod("PUT")
        .withPath(s"$userConfigPathPrefix/checkpoint/collection"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(userConfigJson)))

  mockServer.when(
    request()
        .withMethod("PUT")
        .withPath(s"$userConfigPathPrefix/checkpoint/joined/$joinedById"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(userConfigJson)))

  mockServer.when(
    request()
        .withMethod("PUT")
        .withPath(s"$userConfigPathPrefix/tester"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(userConfigJson)))

}

trait SharedCollectionsServer {

  self: MockServerService =>

  Logging.overrideLogLevel("ERROR")

  mockServer.when(
    request()
        .withMethod("GET")
        .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(sharedCollectionJsonSingle)))

  mockServer.when(
    request()
        .withMethod("GET")
        .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionType/$regexpPath"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(sharedCollectionJsonList)))

  mockServer.when(
    request()
        .withMethod("GET")
        .withPath(s"$sharedCollectionPathPrefix/search/$regexpPath"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(sharedCollectionJsonList)))

  mockServer.when(
    request()
        .withMethod("POST")
        .withPath(sharedCollectionPathPrefix))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(sharedCollectionJsonSingle)))

  mockServer.when(
    request()
        .withMethod("POST")
        .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst/rate/$regexpPath"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(sharedCollectionJsonSingle)))

  mockServer.when(
    request()
        .withMethod("PUT")
        .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst/subscribe"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(sharedCollectionJsonSubscription)))

  mockServer.when(
    request()
        .withMethod("DELETE")
        .withPath(s"$sharedCollectionPathPrefix/$sharedCollectionIdFirst/subscribe"))
      .respond(
        response()
            .withStatusCode(200)
            .withBody(" "))

}

trait GooglePlayServer {

  self: MockServerService =>

  Logging.overrideLogLevel("ERROR")

  mockServer.when(
    request()
        .withMethod("GET")
        .withPath(s"$googlePlayPathPrefix/$packageName1"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(googlePlayPackageJsonSingle)))

  mockServer.when(
    request()
        .withMethod("POST")
        .withPath(googlePlayPackagesPathPrefix))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(googlePlayPackageJsonList)))

  mockServer.when(
    request()
        .withMethod("POST")
        .withPath(googlePlaySimplePackagesPathPrefix))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(googlePlaySimplePackageJsonList)))

  mockServer.when(
    request()
        .withMethod("GET")
        .withPath(s"$googlePlaySearchPathPrefix/$searchQuery/$searchOffset/$searchLimit"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(googlePlaySearchJson)))

}

trait RecommendationsServer {

  self: MockServerService =>

  Logging.overrideLogLevel("ERROR")

  mockServer.when(
    request()
        .withMethod("GET")
        .withPath(s"$recommendationSponsoredPathPrefix"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(recommendationSponsoredJson)))

  mockServer.when(
    request()
        .withMethod("POST")
        .withPath(s"$recommendationsPathPrefix/recommendations/apps"))
      .respond(
        response()
            .withStatusCode(200)
            .withHeader(jsonHeader)
            .withBody(loadJson(recommendedAppsJson)))

}
