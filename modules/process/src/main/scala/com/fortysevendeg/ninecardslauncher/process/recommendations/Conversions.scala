package cards.nine.process.recommendations

import cards.nine.process.recommendations.models.RecommendedApp
import cards.nine.services.api.RecommendationApp

trait Conversions {

  def toRecommendedApp(app: RecommendationApp): RecommendedApp =
    RecommendedApp(
      packageName = app.packageName,
      title = app.name,
      icon = Some(app.icon),
      downloads = app.downloads,
      stars = app.stars,
      free = app.free,
      screenshots = app.screenshots)

}
