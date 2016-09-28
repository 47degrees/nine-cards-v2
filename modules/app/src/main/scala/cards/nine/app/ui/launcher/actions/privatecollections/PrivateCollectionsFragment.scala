package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher2.R

class PrivateCollectionsFragment(implicit lPresenter: LauncherPresenter)
  extends BaseActionFragment
  with PrivateCollectionsActionsImpl
  with NineCardIntentConversions { self =>

  override lazy val collectionPresenter = new PrivateCollectionsPresenter(self)

  lazy val launcherPresenter = lPresenter

  lazy val packages = getSeqString(Seq(getArguments), BaseActionFragment.packages, Seq.empty[String])

  override def getLayoutId: Int = R.layout.list_action_fragment

  override protected lazy val backgroundColor: Int = loadBackgroundColor

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    collectionPresenter.initialize()
  }
}



