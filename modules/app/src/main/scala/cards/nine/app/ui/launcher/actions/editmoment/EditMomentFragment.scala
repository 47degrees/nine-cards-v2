package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.editmoment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.BaseActionFragment
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardsMoment
import com.fortysevendeg.ninecardslauncher2.R

class EditMomentFragment
  extends BaseActionFragment
  with EditMomentActionsImpl
  with NineCardIntentConversions { self =>

  lazy val momentType = Option(getString(Seq(getArguments), EditMomentFragment.momentKey, javaNull))

  override lazy val editPresenter = new EditMomentPresenter(self)

  override def useFab: Boolean = true

  override def getLayoutId: Int = R.layout.edit_moment

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    super.onViewCreated(view, savedInstanceState)
    momentType match {
      case Some(moment) => editPresenter.initialize(NineCardsMoment(moment))
      case _ => editPresenter.momentNoFound()
    }
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data)
    (requestCode, resultCode) match {
      case (RequestCodes.selectInfoWifi, Activity.RESULT_OK) =>
        Option(data) flatMap (d => Option(d.getExtras)) foreach {
          case extras if extras.containsKey(EditMomentFragment.wifiRequest) =>
            editPresenter.addWifi(extras.getString(EditMomentFragment.wifiRequest))
          case _ =>
        }
      case _ =>
    }
  }

}

object EditMomentFragment {

  val momentKey = "moment"

  val wifiRequest = "wifi-request"

}

