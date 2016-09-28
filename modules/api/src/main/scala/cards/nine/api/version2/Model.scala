package cards.nine.api.version2

trait BaseServiceHeader {

  def apiKey: String
  def sessionToken: String
  def androidId: String

}

case class ServiceHeader(
  apiKey: String,
  sessionToken: String,
  androidId: String) extends BaseServiceHeader

case class ServiceMarketHeader(
  apiKey: String,
  sessionToken: String,
  androidId: String,
  androidMarketToken: Option[String]) extends BaseServiceHeader

case class ApiLoginRequest(email: String, androidId: String, tokenId: String)

case class ApiLoginResponse(apiKey: String, sessionToken: String)

case class InstallationRequest(deviceToken: String)

case class InstallationResponse(androidId: String, deviceToken: String)

case class CollectionsResponse(collections: Seq[Collection])

case class CreateCollectionRequest(
  name: String,
  author: String,
  icon: String,
  category: String,
  community: Boolean,
  packages: Seq[String])

case class CreateCollectionResponse(publicIdentifier: String, packagesStats: PackagesStats)

case class UpdateCollectionRequest(
  collectionInfo: Option[CollectionUpdateInfo],
  packages: Option[Seq[String]])

case class UpdateCollectionResponse(publicIdentifier: String, packagesStats: PackagesStats)

case class CategorizeRequest(items: Seq[String])

case class CategorizeResponse(errors: Seq[String], items: Seq[CategorizedApp])

case class CategorizeDetailResponse(errors: Seq[String], items: Seq[CategorizedAppDetail])

case class RecommendationsRequest(excludePackages: Seq[String], limit: Int)

case class RecommendationsResponse(items: Seq[RecommendationApp])

case class RecommendationsByAppsRequest(packages: Seq[String], excludePackages: Seq[String], limit: Int)

case class RecommendationsByAppsResponse(apps: Seq[RecommendationApp])

case class SubscriptionsResponse(subscriptions: Seq[String])

case class RankAppsRequest(items: Map[String, Seq[String]], location: Option[String])

case class RankAppsResponse(items: Map[String, Seq[String]])

case class PackagesStats(added: Int, removed: Option[Int] = None)

case class Collection(
  name: String,
  author: String,
  icon: String,
  category: String,
  community: Boolean,
  publishedOn: String,
  installations: Option[Int],
  views: Option[Int],
  subscriptions: Option[Int],
  publicIdentifier: String,
  appsInfo: Seq[CollectionApp],
  packages: Seq[String])

case class CollectionApp(
  stars: Double,
  icon: String,
  packageName: String,
  downloads: String,
  category: String,
  title: String,
  free: Boolean)

case class CollectionUpdateInfo(
  title: String)

case class CategorizedApp(
  packageName: String,
  category: String)

case class CategorizedAppDetail(
  packageName: String,
  title: String,
  categories: Seq[String],
  icon: String,
  free: Boolean,
  downloads: String,
  stars: Double)

case class RecommendationApp(
  packageName: String,
  title: String,
  downloads: String,
  icon: String,
  stars: Double,
  free: Boolean,
  screenshots: Seq[String])