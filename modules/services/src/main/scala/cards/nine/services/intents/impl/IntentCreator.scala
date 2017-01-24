/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.services.intents.impl

import android.app.SearchManager
import android.content.{ComponentName, Intent}
import android.net.Uri
import android.provider.ContactsContract
import android.speech.RecognizerIntent
import cards.nine.commons._
import cards.nine.commons.contexts.ActivityContextSupport

class IntentCreator {

  val emailType = "message/rfc822"

  val shareType = "text/plain"

  val searchPackageName = "com.google.android.googlequicksearchbox"
  val searchClassName   = "com.google.android.googlequicksearchbox.SearchActivity"

  val googleWeatherUri     = "dynact://velour/weather/ProxyActivity"
  val googleWeatherPackage = "com.google.android.googlequicksearchbox"
  val googleWeatherClass   = "com.google.android.apps.gsa.velour.DynamicActivityTrampoline"

  val googlePlayStorePackage = "com.android.vending"

  def createAppIntent(packageName: String, className: String): Intent = {
    val intent = new Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    intent.setComponent(new ComponentName(packageName, className))
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent
  }

  def createAppLaunchIntent(packageName: String)(
      implicit activityContext: ActivityContextSupport): Intent =
    activityContext.getPackageManager.getLaunchIntentForPackage(packageName)

  def createAppSettingsIntent(packageName: String): Intent =
    createSettingsIntent(Some(packageName))

  def createGlobalSettingsIntent(): Intent =
    createSettingsIntent()

  private[this] def createSettingsIntent(maybePackageName: Option[String] = None): Intent = {
    val intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    maybePackageName foreach (packageName => intent.setData(Uri.parse(s"package:$packageName")))
    intent
  }

  def createAppUninstallIntent(packageName: String): Intent =
    new Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse(s"package:$packageName"))

  def createAppGooglePlayIntent(googlePlayUrl: String, packageName: String): Intent = {
    val intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s"$googlePlayUrl?id=$packageName"))
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent
  }

  def createPhoneSmsIntent(phoneNumber: String): Intent =
    new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, javaNull))

  def createPhoneCallIntent(phoneNumber: String): Intent =
    createCallIntent(Option(phoneNumber), Intent.ACTION_CALL)

  def createPhoneDialIntent(maybePhoneNumber: Option[String]): Intent =
    createCallIntent(maybePhoneNumber, Intent.ACTION_DIAL)

  private[this] def createCallIntent(maybePhoneNumber: Option[String], action: String): Intent = {
    val intent = new Intent(action)
    maybePhoneNumber foreach (phoneNumber => intent.setData(Uri.parse(s"tel:$phoneNumber")))
    intent
  }

  def createEmailIntent(email: String, titleDialog: String): Intent = {
    val newIntent = new Intent(Intent.ACTION_SEND)
    newIntent.setType(emailType)
    newIntent.putExtra(Intent.EXTRA_EMAIL, Array(email))
    Intent.createChooser(newIntent, titleDialog)
  }

  def createContactIntent(lookupKey: String): Intent = {
    val contactUri =
      ContactsContract.Contacts.CONTENT_LOOKUP_URI.buildUpon().appendPath(lookupKey).build()
    new Intent(Intent.ACTION_VIEW, contactUri)
  }

  def createShareIntent(text: String, titleDialog: String): Intent = {
    val intent = new Intent()
    intent.setAction(Intent.ACTION_SEND)
    intent.putExtra(Intent.EXTRA_TEXT, text)
    intent.setType(shareType)
    Intent.createChooser(intent, titleDialog)
  }

  def createSearchGlobalIntent(): Intent = {
    val intent        = new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH)
    val componentName = new ComponentName(searchPackageName, searchClassName)
    intent.setComponent(componentName)
    intent
  }

  def createSearchWebIntent(): Intent = new Intent(Intent.ACTION_WEB_SEARCH)

  def createSearchVoiceIntent(): Intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH)

  def createGoogleWeatherIntent(): Intent = {
    val intent = new Intent(Intent.ACTION_VIEW)
    intent.setData(Uri.parse(googleWeatherUri))
    intent.setClassName(googleWeatherPackage, googleWeatherClass)
    intent
  }

  def createGooglePlayStoreIntent()(implicit activityContext: ActivityContextSupport): Intent =
    createAppLaunchIntent(googlePlayStorePackage)

  def createUrlViewIntent(url: String): Intent = {
    val intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent
  }

}
