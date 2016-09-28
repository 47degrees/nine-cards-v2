package com.fortysevendeg.ninecardslauncher.commons.contexts

import java.io.File

import android.accounts.AccountManager
import android.app.Activity
import android.content.{ContentResolver, Context, Intent, SharedPreferences}
import android.content.pm.PackageManager
import android.content.res.{AssetManager, Resources}

import scala.ref.WeakReference

trait ContextSupport {
  def context: Context
  def getOriginal: WeakReference[Context]
  def getPackageManager: PackageManager
  def getResources: Resources
  def getContentResolver: ContentResolver
  def getFilesDir: File
  def getAppIconsDir: File
  def getAssets: AssetManager
  def getPackageName: String
  def getSharedPreferences: SharedPreferences
  def getActiveUserId: Option[Int]
  def setActiveUserId(id: Int): Unit
  def getAccountManager: AccountManager
  def createIntent(classOf: Class[_]): Intent
}

trait ActivityContextSupport extends ContextSupport {
  def getActivity: Option[Activity]
}