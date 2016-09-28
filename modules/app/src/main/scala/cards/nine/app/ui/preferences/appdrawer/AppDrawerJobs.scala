package com.fortysevendeg.ninecardslauncher.app.ui.preferences.appdrawer

import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService
import macroid.ContextWrapper

class AppDrawerJobs(ui: AppDrawerUiActions)(implicit contextWrapper: ContextWrapper)
  extends Jobs {

  def initialize(): TaskService[Unit] = ui.initialize()

}
