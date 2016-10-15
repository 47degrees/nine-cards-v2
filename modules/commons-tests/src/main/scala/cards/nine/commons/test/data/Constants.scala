package cards.nine.commons.test.data

import cards.nine.commons.test.data.CollectionValues._
import cards.nine.models.types._

object ApplicationValues {

  val item = 1
  val items = 5

  val keyword = "fake-keyword"

  val applicationId: Int = 1
  val applicationName: String = "applicationName"
  val applicationPackageName: String = "applicationPackageName"
  val nonExistentApplicationPackageName: String = "nonExistentApplicationPackageName"
  val applicationClassName: String = "applicationClassName"
  val applicationCategoryStr: String = "MISC"
  val applicationCategory: NineCardsCategory = NineCardsCategory(appsCategoryStr)
  val dateInstalled: Long = 1l
  val dateUpdated: Long = 2l
  val version: String = "version"
  val installedFromGooglePlay: Boolean = true

}

object CollectionValues {

  val collectionId: Int = 1
  val nonExistentCollectionId: Int = 10001
  val collectionPosition: Int = 1
  val collectionNewPosition: Int = 5
  val nonExistentCollectionPosition: Int = 10001
  val collectionName: String = "collectionName"
  val collectionType: CollectionType = FreeCollectionType
  val icon: String = "icon"
  val themedColorIndex: Int = 1
  val appsCategoryStr: String = "MISC"
  val appsCategory: NineCardsCategory = NineCardsCategory(appsCategoryStr)
  val originalSharedCollectionId: String = "originalSharedCollection"
  val sharedCollectionId: String = "shareCollectionId"
  val sharedCollectionSubscribed: Boolean = false
  val publicCollectionStatus: PublicCollectionStatus = NotPublished

  val deletedCollection: Int = 1
  val deletedCollections: Int = 2
  val updatedCollection: Int = 1
  val updatedCollections: Int = 2
  val nameCollectionRequest: String = "nameCollectionRequest"
  val iconCollectionRequest: String = "iconCollectionRequest"
  val themedColorIndexRequest: Int = 1
  val newSharedCollectionId: String = "newSharedCollectionId"
  val nonExistentSharedCollectionId: String = "nonExistentSharedCollectionId"

}

object CardValues {

  val cardId: Int = 1
  val nonExistentCardId: Int = 10001
  val cardPosition: Int = 1
  val term: String = "cardTerm"
  val cardPackageName: String = "cardPackageName"
  val cardType: String = "APP"
  val cardIntent: String = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val cardImagePath: String = "/card/image/path"
  val notification: String = "notification"

  val cardCollectionId: Int = 1

  val deletedCard: Int = 1
  val deletedCards: Int = 2
  val updatedCard: Int = 1
  val updatedCards: Int = 2

  val cardIdReorder: Int = 1
  val samePositionReorder: Int = 1
  val newPositionReorder: Int = 2
  val newCardName = "newCardName"

}

object DockAppValues {

  val dockAppId: Int = 1
  val nonExistentDockAppId: Int = 10001
  val dockAppName: String = "dockAppName"
  val dockType: String = "APP"
  val dockAppIntent: String = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val dockAppImagePath: String = "/dockApp/image/path"
  val dockAppPosition: Int = 1

  val deletedDockApp: Int = 1
  val deletedDockApps: Int = 5

}


object FormedValues {

  val itemType: String = "itemType"
  val title: String = "title"
  val formedIntent: String = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""
  val uriImage: String = "uriImage"

}

object MomentValues {

  val momentId: Int = 1
  val nonExistentMomentId: Int = 10001
  val momentCollectionId: Int = 1
  val timeslotJson: String = """[{"from":"from1","to":"to1","days":[11,12,13]},{"from":"from2","to":"to2","days":[21,22,23]}]"""
  val wifiSeq: Seq[String] = Seq("wifi 1", "wifi 2", "wifi 2")
  val headphone: Boolean = false
  val momentTypeSeq = Seq("HOME", "WORK", "NIGHT", "WALK", "STUDY", "MUSIC", "CAR", "BIKE", "RUNNING")
  val momentType: NineCardsMoment = NineCardsMoment("HOME")
  val homeAppPackageName = "com.google.android.apps.plus"
  val nightAppPackageName = "com.Slack"
  val workAppPackageName = "com.google.android.apps.photos"
  val transitAppPackageName = "com.google.android.apps.maps"

  val deletedMoment: Int = 1
  val deletedMoments: Int = 2
  val updatedMoment: Int = 1

}

object UserValues {

  val item: Int = 1
  val items: Int = 5

  val userId: Int = 1
  val nonExistentUserId: Int = 10001
  val email: String = "user@email.com"
  val sessionToken: String = "sessionToken"
  val apiKey: String = "apiKey"
  val deviceToken: String = "deviceToken"
  val marketToken: String = "marketToken"
  val deviceName: String = "deviceName"
  val deviceCloudId: String = "deviceCloudId"
  val userName: String = "userName"
  val avatar: String = "avatar"
  val cover: String = "cover"

}

object WidgetValues {

  val widgetId: Int = 1
  val nonExistentWidgetId: Int = 10001
  val widgetMomentId: Int = 1
  val widgetPackageName: String = "widgetPackageName"
  val widgetClassName: String = "widgetClassName"
  val appWidgetId: Int = 1
  val nonExistentAppWidgetId: Int = 10001
  val startX: Int = 0
  val startY: Int = 0
  val spanX: Int = 1
  val spanY: Int = 1
  val widgetType: String = "APP"
  val label: String = "widget label"
  val widgetImagePath: String = "/widget/image/path"
  val widgetIntent: String = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""

  val deletedWidget: Int = 1
  val deletedWidgets: Int = 2
  val updatedWidget: Int = 1
  val updatedWidgets: Int = 2

}