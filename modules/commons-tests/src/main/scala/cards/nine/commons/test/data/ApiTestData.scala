package cards.nine.commons.test.data

import cards.nine.commons.test.data.ApiValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.UserValues._
import cards.nine.models._
import cards.nine.models.types.NineCardsCategory

trait ApiTestData extends ApplicationTestData {

  implicit val requestConfig = RequestConfig(
    apiKey = apiKey,
    sessionToken = sessionToken,
    androidId = androidId,
    marketToken = Option(marketToken))

  val loginResponse = LoginResponse(
    apiKey = apiKey,
    sessionToken = sessionToken)

  val awarenessLocation = Location(
    latitude = latitude,
    longitude = longitude,
    countryCode = Option(countryCode),
    countryName = Option(countryName),
    addressLines = Seq(street, city, postalCode))

  def categorizedPackage(num: Int = 0) = CategorizedPackage(
    packageName = apiPackageName + num,
    category = Option(category))

  val categorizedPackage: CategorizedPackage = categorizedPackage(0)
  val seqCategorizedPackage: Seq[CategorizedPackage]  = Seq(categorizedPackage(0), categorizedPackage(1), categorizedPackage(2))

  val categorizedDetailPackages = seqApplication map { app =>
    CategorizedDetailPackage(
      packageName = app.packageName,
      title = app.name,
      category = Option(app.category),
      icon = apiIcon,
      free = free,
      downloads = downloads,
      stars = stars)
  }

  val seqCategoryAndPackages: Seq[(NineCardsCategory, Seq[String])] =
    (seqApplication map (app => (app.category, app.packageName))).groupBy(_._1).mapValues(_.map(_._2)).toSeq

  val rankApps: Seq[RankApps] = seqCategoryAndPackages map { item =>
    RankApps(
      category = item._1,
      packages = item._2)
  }

  val packagesByCategory =
    seqCategoryAndPackages map { item =>
      PackagesByCategory(
        category = item._1,
        packages = item._2)
    }

  def recommendedApp(num: Int = 0) = RecommendedApp(
    packageName = apiPackageName + num,
    title = apiTitle + num,
    downloads = downloads,
    icon = Option(apiIcon),
    stars = stars,
    free = free,
    screenshots = screenshots)

  val recommendedApp: RecommendedApp = recommendedApp(0)
  val seqRecommendedApp: Seq[RecommendedApp]  = Seq(recommendedApp(0), recommendedApp(1), recommendedApp(2))

}
