package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut
import macroid.{ActivityContextWrapper, Ui}


class ShortcutPresenter(actions: ShortcutUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs {

  def initialize(): Unit = {
    actions.initialize().run
    loadShortcuts()
  }

  def loadShortcuts(): Unit =
    di.deviceProcess.getAvailableShortcuts.resolveAsyncUi2(
      onPreTask = () => actions.showLoading(),
      onResult =  actions.loadShortcuts,
      onException = (ex: Throwable) => actions.showErrorLoadingShortcutsInScreen()
    )

  def configureShortcut(shortcut: Shortcut) = {
    actions.close().run
    activityContextWrapper.getOriginal.startActivityForResult(shortcut.intent, shortcutAdded)
  }

}

trait ShortcutUiActions {

  def initialize(): Ui[Any]

  def showLoading(): Ui[Any]

  def close(): Ui[Any]

  def loadShortcuts(shortcuts: Seq[Shortcut]): Ui[Any]

  def showErrorLoadingShortcutsInScreen(): Ui[Any]

}
