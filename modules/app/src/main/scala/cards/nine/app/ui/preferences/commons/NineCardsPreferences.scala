package com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.PreferencesKeys._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.PreferencesValuesKeys._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

sealed trait NineCardsPreferences {
  val name: String
}

case object LookFeelPreferences extends NineCardsPreferences {
  override val name: String = lookFeelKey
}

case object MomentsPreferences extends NineCardsPreferences {
  override val name: String = momentKey
}

case object AppDrawerPreferences extends NineCardsPreferences {
  override val name: String = appDrawerKey
}

case object SizesPreferences extends NineCardsPreferences {
  override val name: String = sizesKey
}

case object AnimationsPreferences extends NineCardsPreferences {
  override val name: String = animationsKey
}

case object DeveloperPreferences extends NineCardsPreferences {
  override val name: String = developerKey
}

case object AppInfoPreferences extends NineCardsPreferences {
  override val name: String = appInfoKey
}

case object AboutPreferences extends NineCardsPreferences {
  override val name: String = aboutKey
}

case object HelpPreferences extends NineCardsPreferences {
  override val name: String = helpKey
}

sealed trait NineCardsPreferenceValue[T]
  extends NineCardsPreferences {
  val name: String
  val default: T
  def readValue(pref: NineCardsPreferencesValue): T
}

// Moments Preferences

case object ShowClockMoment
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = showClockMoment
  override val default: Boolean = false

  override def readValue(pref: NineCardsPreferencesValue): Boolean = pref.getBoolean(name, default)
}

// Animations Preferences

case object SpeedAnimations
  extends NineCardsPreferenceValue[SpeedAnimationValue] {
  override val name: String = speed
  override val default: SpeedAnimationValue = NormalAnimation

  override def readValue(pref: NineCardsPreferencesValue): SpeedAnimationValue =
    SpeedAnimationValue(pref.getString(name, default.value))

  def getDuration(implicit contextWrapper: ContextWrapper): Int = {
    resGetInteger(readValue(new NineCardsPreferencesValue) match {
      case NormalAnimation => R.integer.anim_duration_normal
      case SlowAnimation => R.integer.anim_duration_slow
      case FastAnimation => R.integer.anim_duration_fast
    })
  }
}

case object CollectionOpeningAnimations
  extends NineCardsPreferenceValue[CollectionOpeningValue] {
  override val name: String = collectionOpening
  override val default: CollectionOpeningValue = CircleOpeningCollectionAnimation

  override def readValue(pref: NineCardsPreferencesValue): CollectionOpeningValue =
    CollectionOpeningValue(pref.getString(name, default.value))
}

case object WorkspaceAnimations
  extends NineCardsPreferenceValue[WorkspaceAnimationValue] {
  override val name: String = workspaceAnimation
  override val default: WorkspaceAnimationValue = HorizontalSlideWorkspaceAnimation

  override def readValue(pref: NineCardsPreferencesValue): WorkspaceAnimationValue =
    WorkspaceAnimationValue(pref.getString(name, default.value))
}

// App Drawer Preferences

case object AppDrawerLongPressAction
  extends NineCardsPreferenceValue[AppDrawerLongPressActionValue] {
  override val name: String = appDrawerLongPressAction
  override val default: AppDrawerLongPressActionValue = AppDrawerLongPressActionOpenKeyboard

  override def readValue(pref: NineCardsPreferencesValue): AppDrawerLongPressActionValue =
    AppDrawerLongPressActionValue(pref.getString(name, default.value))
}

case object AppDrawerAnimation
  extends NineCardsPreferenceValue[AppDrawerAnimationValue] {
  override val name: String = appDrawerAnimation
  override val default: AppDrawerAnimationValue = AppDrawerAnimationCircle

  override def readValue(pref: NineCardsPreferencesValue): AppDrawerAnimationValue =
    AppDrawerAnimationValue(pref.getString(name, default.value))
}

case object AppDrawerFavoriteContactsFirst
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = appDrawerFavoriteContacts
  override val default: Boolean = false

  override def readValue(pref: NineCardsPreferencesValue): Boolean = pref.getBoolean(name, default)
}

case object AppDrawerSelectItemsInScroller
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = appDrawerSelectItemsInScroller
  override val default: Boolean = true

  override def readValue(pref: NineCardsPreferencesValue): Boolean = pref.getBoolean(name, default)
}

// Look & Feel Preferences

case object Theme
  extends NineCardsPreferenceValue[ThemeValue] {
  override val name: String = theme
  override val default: ThemeValue = ThemeLight

  override def readValue(pref: NineCardsPreferencesValue): ThemeValue =
    ThemeValue(pref.getString(name, default.value))

  def getThemeFile(pref: NineCardsPreferencesValue): String = Theme.readValue(pref) match {
    case ThemeLight => "theme_light"
    case ThemeDark => "theme_dark"
  }
}

case object GoogleLogo
  extends NineCardsPreferenceValue[GoogleLogoValue] {
  override val name: String = googleLogo
  override val default: GoogleLogoValue = GoogleLogoTheme

  override def readValue(pref: NineCardsPreferencesValue): GoogleLogoValue =
    GoogleLogoValue(pref.getString(name, default.value))
}

case object FontSize
  extends NineCardsPreferenceValue[FontSizeValue] {
  override val name: String = fontsSize
  override val default: FontSizeValue = FontSizeMedium

  override def readValue(pref: NineCardsPreferencesValue): FontSizeValue =
    FontSizeValue(pref.getString(name, default.value))

  def getSizeResource(implicit contextWrapper: ContextWrapper): Int = {
    readValue(new NineCardsPreferencesValue) match {
      case FontSizeSmall => R.dimen.text_medium
      case FontSizeMedium => R.dimen.text_default
      case FontSizeLarge => R.dimen.text_large
    }
  }

  def getTitleSizeResource(implicit contextWrapper: ContextWrapper): Int = {
    readValue(new NineCardsPreferencesValue) match {
      case FontSizeSmall => R.dimen.text_large
      case FontSizeMedium => R.dimen.text_xlarge
      case FontSizeLarge => R.dimen.text_xxlarge
    }
  }

  def getContactSizeResource(implicit contextWrapper: ContextWrapper): Int = {
    readValue(new NineCardsPreferencesValue) match {
      case FontSizeSmall => R.dimen.text_default
      case FontSizeMedium => R.dimen.text_large
      case FontSizeLarge => R.dimen.text_xlarge
    }
  }

}

case object IconsSize
  extends NineCardsPreferenceValue[IconsSizeValue] {
  override val name: String = iconsSize
  override val default: IconsSizeValue = IconsSizeMedium

  override def readValue(pref: NineCardsPreferencesValue): IconsSizeValue =
    IconsSizeValue(pref.getString(name, default.value))

  def getIconApp(implicit contextWrapper: ContextWrapper): Int = {
    resGetDimensionPixelSize(readValue(new NineCardsPreferencesValue) match {
      case IconsSizeSmall => R.dimen.size_icon_app_small
      case IconsSizeMedium => R.dimen.size_icon_app_medium
      case IconsSizeLarge => R.dimen.size_icon_app_large
    })
  }

  def getIconCollection(implicit contextWrapper: ContextWrapper): Int = {
    resGetDimensionPixelSize(readValue(new NineCardsPreferencesValue) match {
      case IconsSizeSmall => R.dimen.size_group_collection_small
      case IconsSizeMedium => R.dimen.size_group_collection_medium
      case IconsSizeLarge => R.dimen.size_group_collection_large
    })
  }

}

case object CardPadding
  extends NineCardsPreferenceValue[IconsSizeValue] {
  override val name: String = cardPadding
  override val default: IconsSizeValue = IconsSizeMedium

  override def readValue(pref: NineCardsPreferencesValue): IconsSizeValue =
    IconsSizeValue(pref.getString(name, default.value))

  def getPadding(implicit contextWrapper: ContextWrapper): Int = {
    resGetDimensionPixelSize(readValue(new NineCardsPreferencesValue) match {
      case IconsSizeSmall => R.dimen.card_padding_small
      case IconsSizeMedium => R.dimen.card_padding_medium
      case IconsSizeLarge => R.dimen.card_padding_large
    })
  }
}

// Developer Preferences

case object IsDeveloper
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = isDeveloper
  override val default: Boolean = false

  override def readValue(pref: NineCardsPreferencesValue): Boolean = pref.getBoolean(name, default)

  def convertToDeveloper(pref: NineCardsPreferencesValue): Unit = pref.setBoolean(name, value = true)
}

case object AppsCategorized
  extends NineCardsPreferenceValue[String] {
  override val name: String = appsCategorized
  override val default: String = ""

  override def readValue(pref: NineCardsPreferencesValue): String = pref.getString(name, default)
}

case object AndroidToken
  extends NineCardsPreferenceValue[String] {
  override val name: String = androidToken
  override val default: String = ""

  override def readValue(pref: NineCardsPreferencesValue): String = pref.getString(name, default)
}

case object DeviceCloudId
  extends NineCardsPreferenceValue[String] {
  override val name: String = deviceCloudId
  override val default: String = ""

  override def readValue(pref: NineCardsPreferencesValue): String = pref.getString(name, default)
}

case object ProbablyActivity
  extends NineCardsPreferenceValue[String] {
  override val name: String = probablyActivity
  override val default: String = ""

  override def readValue(pref: NineCardsPreferencesValue): String = pref.getString(name, default)
}

case object Headphones
  extends NineCardsPreferenceValue[String] {
  override val name: String = headphones
  override val default: String = ""

  override def readValue(pref: NineCardsPreferencesValue): String = pref.getString(name, default)
}

case object Location
  extends NineCardsPreferenceValue[String] {
  override val name: String = location
  override val default: String = ""

  override def readValue(pref: NineCardsPreferencesValue): String = pref.getString(name, default)
}

case object Weather
  extends NineCardsPreferenceValue[String] {
  override val name: String = weather
  override val default: String = ""

  override def readValue(pref: NineCardsPreferencesValue): String = pref.getString(name, default)
}

case object ClearCacheImages
  extends NineCardsPreferenceValue[String] {
  override val name: String = clearCacheImages
  override val default: String = ""

  override def readValue(pref: NineCardsPreferencesValue): String = pref.getString(name, default)
}

case object ShowPositionInCards
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = showPositionInCards
  override val default: Boolean = false

  override def readValue(pref: NineCardsPreferencesValue): Boolean = pref.getBoolean(name, default)
}

case object ShowPrintInfoOptionInAccounts
  extends NineCardsPreferenceValue[Boolean] {
  override val name: String = showPrintInfoOptionInAccounts
  override val default: Boolean = false

  override def readValue(pref: NineCardsPreferencesValue): Boolean = pref.getBoolean(name, default)
}


// Commons

class NineCardsPreferencesValue(implicit contextWrapper: ContextWrapper) {

  private[this] def get[T](f: (SharedPreferences) => T) =
    f(PreferenceManager.getDefaultSharedPreferences(contextWrapper.application))

  def getInt(name: String, defaultValue: Int): Int = get(_.getInt(name, defaultValue))

  def setInt(name: String, value: Int): Unit = get(_.edit().putInt(name, value).apply())

  def getString(name: String, defaultValue: String): String = get(_.getString(name, defaultValue))

  def setString(name: String, value: String): Unit = get(_.edit().putString(name, value).apply())

  def getBoolean(name: String, defaultValue: Boolean): Boolean = get(_.getBoolean(name, defaultValue))

  def setBoolean(name: String, value: Boolean): Unit = get(_.edit().putBoolean(name, value).apply())

}

// This values should be the same that the keys used in XML preferences_headers
object PreferencesKeys {
  val defaultLauncherKey = "defaultLauncherKey"
  val lookFeelKey = "lookFeelKey"
  val momentKey = "momentKey"
  val appDrawerKey = "appDrawerKey"
  val sizesKey = "sizesKey"
  val animationsKey = "animationsKey"
  val developerKey = "developerKey"
  val aboutKey = "aboutKey"
  val helpKey = "helpKey"
  val appInfoKey = "appInfoKey"

}

// Values for all preference keys used for values
object PreferencesValuesKeys {
  // Moment keys
  val showClockMoment = "showClockMoment"

  // Look and Feel Keys
  val theme = "theme"
  val googleLogo = "googleLogo"
  val fontsSize = "fontsSize"
  val iconsSize = "iconsSize"
  val cardPadding = "cardPadding"

  // AppDrawer Keys
  val appDrawerLongPressAction = "appDrawerLongPressAction"
  val appDrawerAnimation = "appDrawerAnimation"
  val appDrawerFavoriteContacts = "appDrawerFavoriteContacts"
  val appDrawerSelectItemsInScroller = "appDrawerSelectItemsInScroller"

  // Speed
  val speed = "speed"
  val collectionOpening = "collectionOpening"
  val workspaceAnimation = "workspaceAnimation"

  // Developer Preferences
  val isDeveloper = "isDeveloper"
  val appsCategorized = "appsCategorized"
  val androidToken = "androidToken"
  val deviceCloudId = "deviceCloudId"
  val probablyActivity = "probablyActivity"
  val headphones = "headphones"
  val location = "location"
  val weather = "weather"
  val clearCacheImages = "clearCacheImages"
  val showPositionInCards = "showPositionInCards"
  val showPrintInfoOptionInAccounts = "showPrintInfoOptionInAccounts"
}



