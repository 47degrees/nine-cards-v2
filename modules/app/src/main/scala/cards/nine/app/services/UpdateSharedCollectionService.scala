package com.fortysevendeg.ninecardslauncher.app.services

import android.app.{IntentService, NotificationManager, Service}
import android.content.{Context, Intent}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.InjectorImpl
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import macroid.Contexts

import UpdateSharedCollectionService._
import com.fortysevendeg.ninecardslauncher2.R

class UpdateSharedCollectionService
  extends IntentService("updateSharedCollectionService")
  with Contexts[Service]
  with ContextSupportProvider {

  implicit lazy val di = new InjectorImpl

  lazy val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]

  override def onHandleIntent(intent: Intent): Unit = {

    def readIntValue(i: Intent, key: String): Option[Int] =
      if (i.hasExtra(key)) Option(i.getIntExtra(key, 0)) else None

    def readStringValue(i: Intent, key: String): Option[String] =
      if (i.hasExtra(key)) Option(i.getStringExtra(key)) else None

    def readArrayValue(i: Intent, key: String): Array[String] =
      if (i.hasExtra(key)) Option(i.getStringArrayExtra(key)).getOrElse(Array.empty) else Array.empty

    val (collectionId, sharedCollectionId, action, packages) = Option(intent) match {
      case Some(i) => (
        readIntValue(i, intentExtraCollectionId),
        readStringValue(i, intentExtraSharedCollectionId),
        Option(i.getAction),
        readArrayValue(i, intentExtraPackages))
      case _ => (None, None, None, Array.empty[String])
    }

    notifyManager.cancel(notificationId)

    (collectionId, sharedCollectionId, action) match {
      case (_, Some(shareCollectionId), Some(`actionUnsubscribe`)) =>
        di.sharedCollectionsProcess.unsubscribe(shareCollectionId).resolveAsync2(
          onResult = (_) => uiShortToast2(R.string.sharedCollectionUnsubscribed),
          onException = e => printErrorMessage(e))
      case (Some(id), _, Some(`actionSync`)) =>
        di.collectionProcess.addPackages(id, packages.toSeq).resolveAsync2(
          onResult = (_) => uiShortToast2(R.string.sharedCollectionUpdated),
          onException = e => printErrorMessage(e))
      case _ =>
    }

  }
}

object UpdateSharedCollectionService {

  val intentExtraCollectionId = "_collectionId_"

  val intentExtraSharedCollectionId = "_sharedCollectionId_"

  val intentExtraPackages = "_addedPackages_"

  val actionUnsubscribe = "unsubscribeCollection"

  val actionSync = "syncCollection"

  val notificationId: Int = 2101

}