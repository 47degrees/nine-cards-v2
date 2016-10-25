package cards.nine.app.ui.collections.actions.apps

import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.models.TermCounter
import cards.nine.models.types._
import cards.nine.process.device.models.IterableApps
import macroid.ActivityContextWrapper

case class AppsJobs(
  category: NineCardsCategory,
  actions: AppsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with AppNineCardsIntentConversions {

  def initialize(): TaskService[Unit] = {
    val onlyAllApps = category == AllAppsCategory || category == Misc
    for {
      _ <- actions.initialize(onlyAllApps, category)
      _ <- loadApps(if (onlyAllApps) AllApps else AppsByCategory, reload = false)
    } yield ()
  }

  def destroy(): TaskService[Unit] = actions.destroy()

  def loadApps(filter: AppsFilter, reload: Boolean = true): TaskService[Unit] = {

    def getLoadApps(order: GetAppOrder): TaskService[(IterableApps, Seq[TermCounter])] =
      for {
        iterableApps <- di.deviceProcess.getIterableApps(order)
        counters <- di.deviceProcess.getTermCountersForApps(order)
      } yield (iterableApps, counters)

    def getLoadAppsByCategory(category: NineCardsCategory): TaskService[(IterableApps, Seq[TermCounter])] =
      for {
        iterableApps <- di.deviceProcess.getIterableAppsByCategory(category.name)
      } yield (iterableApps, Seq.empty)

    for {
      _ <- actions.showLoading()
      data <- filter match {
        case AllApps => getLoadApps(GetByName)
        case AppsByCategory => getLoadAppsByCategory(category)
      }
      (apps, counters) = data
      _ <- actions.showApps(category, filter, apps, counters, reload)
      isTabsOpened <- actions.isTabsOpened
      _ <- actions.closeTabs().resolveIf(isTabsOpened, ())
    } yield ()
  }

  def showErrorLoadingApps(filter: AppsFilter): TaskService[Unit] = actions.showErrorLoadingAppsInScreen(filter)

  def swapFilter(): TaskService[Unit] =
    for {
      isTabsOpened <- actions.isTabsOpened
      _ <- if (isTabsOpened) actions.closeTabs() else actions.openTabs()
    } yield ()

  def showError(): TaskService[Unit] = actions.showError()

  def close(): TaskService[Unit] = actions.close()

}
