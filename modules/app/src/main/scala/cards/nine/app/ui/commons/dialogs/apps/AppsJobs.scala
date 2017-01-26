/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.commons.dialogs.apps

import cards.nine.app.commons.{AppNineCardsIntentConversions, Conversions}
import cards.nine.app.ui.commons.dialogs.apps.AppsFragment._
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService._
import cards.nine.models._
import cards.nine.models.types._
import macroid.ActivityContextWrapper

case class AppsJobs(actions: AppsUiActions)(
    implicit activityContextWrapper: ActivityContextWrapper)
    extends Jobs
    with AppNineCardsIntentConversions
    with Conversions {

  def initialize(selectedApps: Set[String]): TaskService[Unit] =
    for {
      _ <- actions.initialize(selectedApps)
      _ <- loadApps()
    } yield ()

  def destroy(): TaskService[Unit] = actions.destroy()

  def loadSearch(query: String): TaskService[Unit] = {
    for {
      _      <- actions.showLoadingInGooglePlay()
      result <- di.recommendationsProcess.searchApps(query)
      _      <- actions.reloadSearch(result)
    } yield ()
  }

  def loadApps(): TaskService[Unit] = {

    def getLoadApps(order: GetAppOrder): TaskService[(IterableApplicationData, Seq[TermCounter])] =
      for {
        iterableApps <- di.deviceProcess.getIterableApps(order)
        counters     <- di.deviceProcess.getTermCountersForApps(order)
      } yield (iterableApps, counters)

    for {
      _    <- actions.showSelectedMessageAndFab()
      _    <- actions.showLoading()
      data <- getLoadApps(GetByName)
      (apps, counters) = data
      _ <- actions.showApps(apps, counters)
    } yield ()
  }

  def loadAppsByKeyword(keyword: String): TaskService[Unit] =
    for {
      apps <- di.deviceProcess.getIterableAppsByKeyWord(keyword, GetByName)
      _    <- actions.showApps(apps, Seq.empty)
    } yield ()

  def getAddedAndRemovedApps: TaskService[(Seq[CardData], Seq[CardData])] = {

    val initialPackages  = appStatuses.initialPackages
    val selectedPackages = appStatuses.selectedPackages

    def getCardsFromPackages(
        packageNames: Set[String],
        apps: Seq[ApplicationData]): Seq[CardData] =
      (packageNames flatMap { packageName =>
        apps.find(_.packageName == packageName)
      } map toCardData).toSeq

    for {
      allApps <- di.deviceProcess.getSavedApps(GetByName)
    } yield {
      val cardsToAdd =
        getCardsFromPackages(selectedPackages.diff(initialPackages), allApps)
      val cardsToRemove =
        getCardsFromPackages(initialPackages.diff(selectedPackages), allApps)
      (cardsToAdd, cardsToRemove)
    }
  }

  def updateSelectedApps(packages: Set[String]): TaskService[Unit] =
    actions.showUpdateSelectedApps(packages)

  def launchGooglePlay(packageName: String): TaskService[Unit] =
    di.launcherExecutorProcess.launchGooglePlay(packageName)

  def showErrorLoadingApps(): TaskService[Unit] =
    actions.showErrorLoadingAppsInScreen()

  def showError(): TaskService[Unit] = actions.showError()

  def close(): TaskService[Unit] = actions.close()

}
