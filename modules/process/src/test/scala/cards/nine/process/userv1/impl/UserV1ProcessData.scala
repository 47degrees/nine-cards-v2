package cards.nine.process.userv1.impl

import cards.nine.process.commons.types.{AppCardType, AppsCollectionType, Game}
import cards.nine.services.api.{GetUserV1Response, LoginResponseV1, RequestConfigV1}
import cards.nine.services.api.models._
import cards.nine.services.persistence.models.{User => PersistenceUser}
import play.api.libs.json.JsString

trait UserV1ProcessData {

  val statusCodeUser = 101

  val userId = 99

  val sessionToken = "fake-user-token"

  val email = "example@47deg.com"

  val name = "George"

  val imageUrl = "http://www.47deg.com/image.jpg"

  val deviceIdPrefix = "fake-device-id"

  val deviceName = "Nexus"

  val collectionName = "Example Collection"

  val collectionIcon = "GAME"

  val collectionType = AppsCollectionType

  val collectionCategory = Game

  val itemType = AppCardType

  val deviceId = "XX-47-XX"

  val marketToken = "EE-47-47-EE"

  val permissions = Seq.empty

  val googleDevice = LoginV1Device(
    name = deviceName,
    deviceId = deviceId,
    secretToken = marketToken,
    permissions = permissions)

  val persistenceUser = PersistenceUser(
    id = userId,
    sessionToken = Some(sessionToken),
    email = Some(email),
    apiKey = None,
    deviceToken = None,
    marketToken = Some(marketToken),
    name = None,
    avatar = None,
    cover = None,
    deviceName = None,
    deviceCloudId = None)

  val loginResponseV1 = LoginResponseV1(
    statusCode = statusCodeUser,
    userId = Option(userId.toString),
    sessionToken = Option(sessionToken),
    email = Option(email),
    devices = Seq(googleDevice))

  def createUserConfigCollectionItem(count: Int = 8): Seq[UserV1CollectionItem] =
    (0 until count) map {
      item =>
        UserV1CollectionItem(
          itemType = itemType.name,
          title = s"Item $item",
          metadata = JsString(""),
          categories = Option(Seq.empty)
        )
    }

  def createUserConfigCollection(count: Int = 5) =
    (0 until count) map {
      item =>
        UserV1Collection(
          name = collectionName,
          originalSharedCollectionId = None,
          sharedCollectionId = None,
          sharedCollectionSubscribed = Option(true),
          items = createUserConfigCollectionItem(),
          collectionType = collectionType.name,
          constrains = Seq.empty,
          wifi = Seq.empty,
          occurrence = Seq.empty,
          icon = collectionIcon,
          radius = 0,
          lat = 0,
          lng = 0,
          alt = 0,
          category = Option(collectionCategory.name)
        )
    }

  def createUserConfigDevice(count: Int = 3) =
    (0 until count) map {
      item =>
        UserV1Device(
          deviceId = s"$deviceIdPrefix-$item",
          deviceName = s"$deviceName $item",
          collections = createUserConfigCollection()
        )
    }

  val userConfig = UserV1(
    _id = "fake-id",
    email = email,
    plusProfile = UserV1PlusProfile(
      displayName = name,
      profileImage = UserV1ProfileImage(
        imageType = 0,
        imageUrl = imageUrl
      )
    ),
    devices = createUserConfigDevice(),
    status = UserV1StatusInfo(
      products = Seq.empty,
      friendsReferred = 0,
      themesShared = 0,
      collectionsShared = 0,
      customCollections = 0,
      earlyAdopter = false,
      communityMember = true,
      joinedThrough = None,
      tester = false))

  val getUserConfigResponse = GetUserV1Response(
    statusCode = statusCodeUser,
    userConfig = userConfig)

  val requestConfig = RequestConfigV1(
    deviceId = deviceId,
    token = sessionToken,
    marketToken = Some(marketToken))

}
