package com.fortysevendeg.ninecardslauncher.api.model

case class UserConfig(
    _id: String,
    email: String,
    plusProfile: UserConfigPlusProfile,
    devices: Seq[UserConfigDevice],
    geoInfo: UserConfigGeoInfo,
    status: UserConfigStatusInfo)

case class UserConfigPlusProfile(
    displayName: String,
    profileImage: UserConfigProfileImage)

case class UserConfigDevice(
    deviceId: String,
    deviceName: String,
    collections: Seq[UserConfigCollection])

case class UserConfigGeoInfo(
    homeMorning: Option[UserConfigUserLocation],
    homeNight: Option[UserConfigUserLocation],
    work: Option[UserConfigUserLocation],
    current: Option[UserConfigUserLocation])

case class UserConfigStatusInfo(
    products: Seq[String],
    friendsReferred: Int,
    themesShared: Int,
    collectionsShared: Int,
    customCollections: Int,
    earlyAdopter: Boolean,
    communityMember: Boolean,
    joinedThrough: String,
    tester: Boolean)

case class UserConfigProfileImage(
    imageType: Int,
    imageUrl: String,
    secureUrl: String)

case class UserConfigCollection(
    name: String,
    originalSharedCollectionId: Option[String],
    sharedCollectionId: Option[String],
    sharedCollectionSubscribed: Option[String],
    items: Seq[UserConfigCollectionItem],
    collectionType: String,
    constrains: Seq[String],
    wifi: Seq[String],
    occurrence: Seq[String],
    icon: String,
    radius: Int,
    lat: Double,
    lng: Double,
    alt: Double,
    category: String)

case class UserConfigCollectionItem(
    itemType: String,
    title: String,
    metadata: NineCardIntent,
    categories: Option[Seq[String]])

case class NineCardIntent(
    action: String,
    className: Option[String],
    packageName: Option[String],
    dataExtra: Option[String],
    intentExtras: Option[Map[String, String]],
    categories: Option[Seq[String]])

case class UserConfigUserLocation(
    wifi: String,
    lat: Double,
    lng: Double,
    occurrence: Seq[UserConfigTimeSlot])

case class UserConfigTimeSlot(
    from: String,
    to: String,
    days: Seq[Int])