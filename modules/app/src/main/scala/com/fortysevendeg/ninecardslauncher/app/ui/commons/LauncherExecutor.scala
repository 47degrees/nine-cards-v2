package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.content.{ComponentName, Intent}
import android.net.Uri
import android.widget.Toast
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardsIntentExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper

import scala.util.{Failure, Success, Try}

trait LauncherExecutor {

  val typeEmail = "message/rfc822"

  val titleDialogEmail = "Send Email"

  def execute(intent: NineCardIntent)(implicit activityContext: ActivityContextWrapper) = {
    intent.getAction match {
      case `openApp` =>
        (for {
          newIntent <- createIntentForApp(intent)
          activity <- activityContext.original.get
        } yield {
            activity.startActivity(newIntent)
          }) getOrElse tryLaunchPackage(intent)
      case `openRecommendedApp` => goToGooglePlay(intent)
      case `openSms` =>
        (for {
          phone <- intent.extractPhone()
          activity <- activityContext.original.get
        } yield {
            val newIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null))
            activity.startActivity(newIntent)
          }) getOrElse showError
      case `openPhone` =>
        (for {
          phone <- intent.extractPhone()
          activity <- activityContext.original.get
        } yield {
          val newIntent = new Intent(Intent.ACTION_CALL) // TODO Preference for select dial new Intent(Intent.ACTION_DIAL)
          newIntent.setData(Uri.parse(s"tel:$phone"))
          activity.startActivity(newIntent)
        }) getOrElse showError
      case `openEmail` =>
        (for {
          email <- intent.extractEmail()
          activity <- activityContext.original.get
        } yield {
            val newIntent = new Intent(Intent.ACTION_SEND)
            newIntent.setType(typeEmail)
            newIntent.putExtra(Intent.EXTRA_EMAIL, email)
            activity.startActivity(Intent.createChooser(newIntent, titleDialogEmail))
          }) getOrElse showError
      case _ => activityContext.getOriginal.startActivity(intent)
    }
  }

  private[this] def createIntentForApp(intent: NineCardIntent): Option[Intent] = for {
    packageName <- intent.extractPackageName()
    className <- intent.extractClassName()
  } yield {
      val intent = new Intent(Intent.ACTION_MAIN)
      intent.addCategory(Intent.CATEGORY_LAUNCHER)
      intent.setComponent(new ComponentName(packageName, className))
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
      intent
    }

  private[this] def tryLaunchPackage(intent: NineCardIntent)(implicit activityContext: ActivityContextWrapper) =
    intent.extractPackageName() match {
      case Some(pn) =>
        Try {
          val newIntent = activityContext.application.getPackageManager.getLaunchIntentForPackage(pn)
          activityContext.getOriginal.startActivity(newIntent)
        } match {
          case Success(_) =>
          case Failure(ex) => goToGooglePlay(intent)
        }
      case _ => showError
    }

  private[this] def goToGooglePlay(intent: NineCardIntent)(implicit activityContext: ActivityContextWrapper) =
    intent.extractPackageName() match {
      case Some(pn) =>
        val newIntent = new Intent(Intent.ACTION_VIEW,
          Uri.parse(activityContext.application.getString(R.string.google_play_url, pn)))
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        Try(activityContext.getOriginal.startActivity(newIntent)) match {
          case Success(_) =>
          case Failure(ex) => showError
        }
      case _ => showError
    }

  private[this] def showError(implicit activityContext: ActivityContextWrapper) =
    Toast.makeText(activityContext.application, R.string.contactUsError, Toast.LENGTH_SHORT).show()

}
