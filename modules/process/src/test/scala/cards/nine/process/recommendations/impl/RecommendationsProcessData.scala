package cards.nine.process.recommendations.impl

import cards.nine.process.commons.types.Social
import cards.nine.services.api.{RecommendationApp, RequestConfig}

import scala.util.Random

trait RecommendationsProcessData {

  val requestConfig = RequestConfig("fake-api-key", "fake-session-token", "fake-android-id", Some("fake-market-token"))

  val statusCodeOk = 200

  val category = Social

  val likePackages = Seq("com.fortysevendeg.package1", "com.fortysevendeg.package2", "com.fortysevendeg.package3")

  val limit = 20

  def generateRecommendationApps() = 1 to 10 map { i =>
    RecommendationApp(
      packageName = Random.nextString(10),
      name = Random.nextString(10),
      downloads = "500,000,000+",
      icon = Random.nextString(10),
      stars = Random.nextDouble() * 5,
      free = Random.nextBoolean(),
      screenshots = Seq("screenshot1", "screenshot2", "screenshot3"))
  }

}
