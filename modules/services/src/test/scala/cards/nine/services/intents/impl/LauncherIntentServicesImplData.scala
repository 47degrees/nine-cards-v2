package cards.nine.services.intents.impl

import cards.nine.models._

trait LauncherIntentServicesImplData {

  val runtimeException = new RuntimeException("Irrelevant Message")
  val securityException = new SecurityException("Irrelevant Message")

  val packageName = "package.name"
  val className = "class.Name"
  val googlePlayUrl = "http://googlePlayUrl"
  val url = "http://mockUrl"
  val lookupKey = "lookupKey"
  val email = "email@google.com"
  val titleDialog = "Dialog Tile"
  val phoneNumber = "666 66 66 66"
  val shareText = "Share text"

  val appAction = AppAction(packageName, className)
  val appGooglePlayAction = AppGooglePlayAction(googlePlayUrl, packageName)
  val appLauncherAction = AppLauncherAction(packageName)
  val appSettingsAction = AppSettingsAction(packageName)
  val appUninstallAction = AppUninstallAction(packageName)
  val contactAction = ContactAction(lookupKey)
  val emailAction = EmailAction(email, titleDialog)
  val globalSettingsAction = GlobalSettingsAction
  val googlePlayStoreAction = GooglePlayStoreAction
  val googleWeatherAction = GoogleWeatherAction
  val phoneSmsAction = PhoneSmsAction(phoneNumber)
  val phoneCallAction = PhoneCallAction(phoneNumber)
  val phoneDialAction = PhoneDialAction(Some(phoneNumber))
  val searchGlobalAction = SearchGlobalAction
  val searchVoiceAction = SearchVoiceAction
  val searchWebAction = SearchWebAction
  val shareAction = ShareAction(shareText, titleDialog)
  val urlAction = UrlAction(url)

}
