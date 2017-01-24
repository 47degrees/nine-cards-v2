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

package cards.nine.app.ui.commons.dialogs.recommendations

import android.app.Dialog
import cards.nine.app.commons.{AppNineCardsIntentConversions, Conversions}
import cards.nine.app.ui.collections.jobs.{GroupCollectionsJobs, SingleCollectionJobs}
import cards.nine.app.ui.commons.AppLog
import cards.nine.app.ui.commons.dialogs.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.NotCategorizedPackage
import cards.nine.models.types.NineCardsCategory
import cards.nine.process.recommendations.RecommendedAppsConfigurationException
import com.fortysevendeg.ninecardslauncher.R

class RecommendationsFragment(
    implicit groupCollectionsJobs: GroupCollectionsJobs,
    singleCollectionJobs: Option[SingleCollectionJobs])
    extends BaseActionFragment
    with RecommendationsUiActions
    with RecommendationsDOM
    with RecommendationsUiListener
    with Conversions
    with AppNineCardsIntentConversions { self =>

  lazy val nineCardCategory = NineCardsCategory(
    getString(Seq(getArguments), RecommendationsFragment.categoryKey, ""))

  lazy val packages =
    getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  lazy val recommendationsJobs =
    new RecommendationsJobs(nineCardCategory, packages, self)

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int = loadBackgroundColor

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    recommendationsJobs.initialize().resolveAsyncServiceOr(onError)
  }

  override def loadRecommendations(): Unit =
    recommendationsJobs.loadRecommendations().resolveAsyncServiceOr(onError)

  override def addApp(app: NotCategorizedPackage): Unit =
    (for {
      cards <- groupCollectionsJobs.addCards(Seq(toCardData(app)))
      _ <- singleCollectionJobs match {
        case Some(job) => job.addCards(cards)
        case _         => TaskService.empty
      }
      _ <- recommendationsJobs.close()
    } yield ()).resolveAsyncServiceOr(_ => recommendationsJobs.showError())

  override def installApp(app: NotCategorizedPackage): Unit =
    recommendationsJobs.installNow(app).resolveAsyncServiceOr(_ => recommendationsJobs.showError())

  private[this] def onError(e: Throwable): TaskService[Unit] = e match {
    case e: RecommendedAppsConfigurationException =>
      AppLog.invalidConfigurationV2
      recommendationsJobs.showErrorLoadingRecommendation()
    case _ =>
      recommendationsJobs.showErrorLoadingRecommendation()
  }
}

object RecommendationsFragment {
  val categoryKey = "category"
}
