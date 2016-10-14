package cards.nine.app.ui.collections.actions.shortcuts

import android.os.Bundle
import android.view.View
import cards.nine.app.commons.AppNineCardIntentConversions
import cards.nine.app.ui.commons.actions.BaseActionFragment
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.models.Shortcut
import com.fortysevendeg.ninecardslauncher.R

class ShortcutFragment
  extends BaseActionFragment
  with ShortcutUiActions
  with ShortcutsDOM
  with ShortcutsUiListener
  with AppNineCardIntentConversions { self =>

  lazy val shortcutJobs = new ShortcutJobs(self)

  override def getLayoutId: Int = R.layout.list_action_fragment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    shortcutJobs.initialize().resolveAsyncServiceOr(_ => shortcutJobs.showErrorLoadingShortcuts())
  }

  override def loadShortcuts(): Unit =
    shortcutJobs.loadShortcuts().resolveAsyncServiceOr(_ => shortcutJobs.showErrorLoadingShortcuts())

  def onConfigure(shortcut: Shortcut): Unit = shortcutJobs.configureShortcut(shortcut).resolveAsync()
}
