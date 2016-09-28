package com.fortysevendeg.ninecardslauncher.services.shortcuts

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.services.shortcuts.models.Shortcut

trait ShortcutsServices {

  /**
   * Get the applications that contains shortcuts to perform specific functions within an app
   * @return the Seq[com.fortysevendeg.ninecardslauncher.services.shortcuts.models.Shortcut] contains
   *         information about shortcut for install it, get the icon, etc
   * @throws ShortcutServicesException if exist some problem to get the shortcuts in the cell phone
   */
  def getShortcuts(implicit context: ContextSupport): TaskService[Seq[Shortcut]]
}
