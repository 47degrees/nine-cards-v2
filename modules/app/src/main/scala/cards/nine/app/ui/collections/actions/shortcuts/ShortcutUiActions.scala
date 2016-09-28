package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts

import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut
import com.fortysevendeg.ninecardslauncher.process.theme.models.DrawerBackgroundColor
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

import scala.math.Ordering.Implicits._

trait ShortcutUiActions
  extends Styles {

  self: BaseActionFragment with ShortcutsDOM with ShortcutsUiListener =>

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.shortcuts) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~
        recyclerStyle <~
        vBackgroundColor(theme.get(DrawerBackgroundColor)))).toService

  def showLoading(): TaskService[Unit] = ((loading <~ vVisible) ~ (recycler <~ vGone)).toService

  def close(): TaskService[Unit] = unreveal().toService

  def configureShortcut(shortcut: Shortcut): TaskService[Unit] = goToConfigureShortcut(shortcut).toService

  def showErrorLoadingShortcutsInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.errorLoadingShortcuts, error = true, loadShortcuts()).toService

  def loadShortcuts(shortcuts: Seq[Shortcut]): TaskService[Unit] = {
    val sortedShortcuts = shortcuts sortBy sortByTitle
    val adapter = ShortcutAdapter(sortedShortcuts, onConfigure)
    ((recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)).toService
  }

  private[this] def sortByTitle(shortcut: Shortcut) = shortcut.title map (c => if (c.isUpper) 2 * c + 1 else 2 * (c - ('a' - 'A')))

}