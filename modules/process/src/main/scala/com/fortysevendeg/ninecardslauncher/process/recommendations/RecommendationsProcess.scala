package com.fortysevendeg.ninecardslauncher.process.recommendations

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp

trait RecommendationsProcess {

  /**
    * Get recommended apps based on a category
    *
    * @param category a valid category identification
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp]
    * @throws RecommendedAppsConfigurationException if there was an error with the API configuration
    * @throws RecommendedAppsException if there was an error fetching the recommended apps
    */
  def getRecommendedAppsByCategory(category: NineCardCategory, excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport): TaskService[Seq[RecommendedApp]]

  /**
    * Get recommended apps based on a category
    *
    * @param packages a valid list of packages
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.recommendations.models.RecommendedApp]
    * @throws RecommendedAppsConfigurationException if there was an error with the API configuration
    * @throws RecommendedAppsException if there was an error fetching the recommended apps
    */
  def getRecommendedAppsByPackages(packages: Seq[String], excludePackages: Seq[String] = Seq.empty)(implicit context: ContextSupport): TaskService[Seq[RecommendedApp]]
}
