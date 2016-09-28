package cards.nine.app.ui.collections.actions.shortcuts

import cards.nine.app.ui.commons.RequestCodes._
import cards.nine.process.device.models.Shortcut
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}
import cards.nine.app.ui.commons.SafeUi._
import macroid.{ActivityContextWrapper, Ui}

trait ShortcutsDOM {

  self: TypedFindView =>

  lazy val recycler = findView(TR.actions_recycler)

  def goToConfigureShortcut(shortcut: Shortcut)(implicit activityContextWrapper: ActivityContextWrapper): Ui[Any] =
    uiStartIntentForResult(shortcut.intent, shortcutAdded)

}

trait ShortcutsUiListener {

  def loadShortcuts(): Unit

  def onConfigure(shortcut: Shortcut): Unit

}