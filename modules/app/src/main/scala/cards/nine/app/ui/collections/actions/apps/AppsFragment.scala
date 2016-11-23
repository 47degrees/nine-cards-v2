package cards.nine.app.ui.collections.actions.apps

import android.app.Dialog
import cards.nine.app.commons.Conversions
import cards.nine.app.ui.collections.actions.apps.AppsFragment._
import cards.nine.app.ui.collections.jobs.{GroupCollectionsJobs, SingleCollectionJobs}
import cards.nine.app.ui.commons.UiExtensions
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{ApplicationData, NotCategorizedPackage}
import com.fortysevendeg.ninecardslauncher.R

class AppsFragment(implicit groupCollectionsJobs: GroupCollectionsJobs, singleCollectionJobs: Option[SingleCollectionJobs])
  extends BaseActionFragment
  with AppsUiActions
  with AppsDOM
  with AppsUiListener
  with Conversions
  with UiExtensions { self =>

  lazy val appsJobs = AppsJobs(actions = self)

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String]).toSet

  override def useFab: Boolean = true

  override def getLayoutId: Int = R.layout.list_action_apps_fragment

  override def setupDialog(dialog: Dialog, style: Int): Unit = {
    super.setupDialog(dialog, style)
    appStatuses = appStatuses.copy(initialPackages = packages, selectedPackages = packages)
    appsJobs.initialize(appStatuses.selectedPackages).resolveAsync()
  }

  override def onDestroy(): Unit = {
    appsJobs.destroy().resolveAsync()
    super.onDestroy()
  }

  override def loadApps(): Unit = {
    appStatuses = appStatuses.copy(contentView = AppsView)
    appsJobs.loadApps().resolveAsyncServiceOr(_ => appsJobs.showErrorLoadingApps())
  }

  override def loadFilteredApps(keyword: String): Unit =
    appsJobs.loadAppsByKeyword(keyword).resolveAsyncServiceOr(_ => appsJobs.showErrorLoadingApps())

  override def loadSearch(query: String): Unit = {
    appStatuses = appStatuses.copy(contentView = GooglePlayView)
    appsJobs.loadSearch(query).resolveAsyncServiceOr(_ => appsJobs.showErrorLoadingApps())
  }

  override def launchGooglePlay(app: NotCategorizedPackage): Unit =
    (for {
      _ <- appsJobs.launchGooglePlay(app.packageName)
      cards <- groupCollectionsJobs.addCards(Seq(toCardData(app)))
      _ <- singleCollectionJobs match {
        case Some(job) => job.addCards(cards)
        case _ => TaskService.empty
      }
      _ <- appsJobs.close()
    } yield ()).resolveAsyncServiceOr(_ => appsJobs.showErrorLoadingApps())

  override def updateSelectedApps(app: ApplicationData): Unit = {
    appStatuses = appStatuses.update(app.packageName)
    appsJobs.updateSelectedApps(appStatuses.selectedPackages).resolveAsyncServiceOr(_ => appsJobs.showError())
  }

  override def updateCollectionApps(): Unit = {

    def updateCards(): TaskService[Unit]  =
      for {
        result <- appsJobs.getAddedAndRemovedApps
        (cardsToAdd, cardsToRemove) = result
        cardsRemoved <- groupCollectionsJobs.removeCardsByPackagesName(cardsToRemove flatMap (_.packageName))
        _ <- singleCollectionJobs match {
          case Some(job) => job.removeCards(cardsRemoved)
          case _ => TaskService.empty
        }
        cardsAdded <- groupCollectionsJobs.addCards(cardsToAdd)
        _ <- singleCollectionJobs match {
          case Some(job) => job.addCards(cardsAdded)
          case _ => TaskService.empty
        }
      } yield ()

    (for {
      _ <- if (appStatuses.initialPackages == appStatuses.selectedPackages) TaskService.empty else updateCards()
      _ <- appsJobs.close()
    } yield ()).resolveAsyncServiceOr(_ => appsJobs.showError())

  }

}

object AppsFragment {

  var appStatuses = AppsStatuses()

  val categoryKey = "category"
}

case class AppsStatuses(
  initialPackages: Set[String] = Set.empty,
  selectedPackages: Set[String] = Set.empty,
  contentView: ContentView = AppsView) {

  def update(packageName: String): AppsStatuses =
    if (selectedPackages.contains(packageName)) copy(selectedPackages = selectedPackages - packageName)
    else copy(selectedPackages = selectedPackages + packageName)

}

sealed trait ContentView

case object AppsView extends ContentView

case object GooglePlayView extends ContentView