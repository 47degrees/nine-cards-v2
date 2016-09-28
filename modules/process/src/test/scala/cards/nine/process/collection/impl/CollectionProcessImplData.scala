package cards.nine.process.collection.impl

import cards.nine.process.collection.models._
import cards.nine.process.collection.{CollectionProcessConfig, AddCardRequest, AddCollectionRequest, EditCollectionRequest}
import cards.nine.process.commons.Spaces._
import cards.nine.process.commons.models.NineCardIntentImplicits._
import cards.nine.process.commons.models._
import cards.nine.process.commons.types.CardType._
import cards.nine.process.commons.types.CollectionType._
import cards.nine.process.commons.types.NineCardCategory._
import cards.nine.process.commons.types._
import cards.nine.services.api.{RankAppsResponse, RankAppsResponseList, CategorizedDetailPackage}
import cards.nine.services.apps.models.Application
import cards.nine.services.awareness.AwarenessLocation
import cards.nine.services.commons.PhoneHome
import cards.nine.services.contacts.models.{Contact => ServicesContact, ContactInfo => ServicesContactInfo, ContactPhone => ServicesContactPhone}
import cards.nine.services.persistence.models.{App => ServicesApp, Card => ServicesCard, Collection => ServicesCollection}
import cards.nine.services.persistence.{UpdateCardRequest => ServicesUpdateCardRequest, UpdateCardsRequest => ServicesUpdateCardsRequest}
import play.api.libs.json.Json

import scala.util.Random

trait CollectionProcessImplData {

  val collectionId = Random.nextInt(10)
  val nonExistentCollectionId = Random.nextInt(10) + 100
  val name: String = Random.nextString(5)
  val collectionType: CollectionType = collectionTypes(Random.nextInt(collectionTypes.length))
  val icon: String = Random.nextString(5)
  val themedColorIndex: Int = Random.nextInt(10)
  val appsCategory: NineCardCategory = appsCategories(Random.nextInt(appsCategories.length))
  val appsCategoryName = appsCategory.name
  val originalSharedCollectionId: String = Random.nextString(5)
  val sharedCollectionId: String = Random.nextString(5)

  val name1 = "Scala Android"
  val packageName1 = "com.fortysevendeg.scala.android"
  val className1 = "ScalaAndroidActivity"
  val path1 = "/example/path1"
  val category1 = "category1"
  val dateInstalled1 = 1L
  val dateUpdate1 = 1L
  val version1 = "22"
  val installedFromGooglePlay1 = true

  val cardId = Random.nextInt(10)
  val position: Int = Random.nextInt(10)
  val newPosition: Int = position + Random.nextInt(10) + 1
  val oldPosition: Int = Random.nextInt(10)
  val term: String = Random.nextString(5)
  val packageName = "package.name."
  def generatePackageName = packageName + Random.nextInt(10)
  val className = Random.nextString(5)
  val cardType: CardType = cardTypes(Random.nextInt(cardTypes.length))
  val imagePath: String = Random.nextString(5)
  val ratingsCount = Random.nextInt()
  val commentCount = Random.nextInt()
  val notification: String = Random.nextString(5)
  val intent = """{ "className": "classNameValue", "packageName": "packageNameValue", "categories": ["category1"], "action": "actionValue", "extras": { "pairValue": "pairValue", "empty": false, "parcelled": false }, "flags": 1, "type": "typeValue"}"""

  val lookupKey: String = Random.nextString(5)
  val photoUri: String = Random.nextString(10)
  val phoneNumber: String = Random.nextString(5)

  val collectionsRemoved = Random.nextInt(2)
  val cardsRemoved = Random.nextInt(2)

  val momentId = Random.nextInt(5)
  val momentType = Random.nextString(5)

  val startX: Int = Random.nextInt(8)
  val startY: Int = Random.nextInt(8)
  val spanX: Int = Random.nextInt(8)
  val spanY: Int = Random.nextInt(8)

  val statusCodeOk = 200

  val latitude: Double = Random.nextDouble()
  val longitude: Double = Random.nextDouble()

  def generateOptionId(id: String) =
    Random.nextBoolean() match {
      case true => None
      case false => Some(id)
    }

  val sharedCollectionIdOption = generateOptionId(sharedCollectionId)
  val originalSharedCollectionIdOption = generateOptionId(originalSharedCollectionId)
  val sharedCollectionSubscribed: Boolean =
    if (sharedCollectionId == originalSharedCollectionId) Random.nextBoolean()
    else false

  def determinePublicCollectionStatus(): PublicCollectionStatus =
    if (sharedCollectionIdOption.isDefined && sharedCollectionSubscribed) Subscribed
    else if (sharedCollectionIdOption.isDefined && originalSharedCollectionIdOption == sharedCollectionIdOption) PublishedByOther
    else if (sharedCollectionIdOption.isDefined) PublishedByMe
    else NotPublished

  val publicCollectionStatus = determinePublicCollectionStatus()

  val application1 = Application(
    name = name1,
    packageName = packageName1,
    className = className1,
    dateInstalled = dateInstalled1,
    dateUpdate = dateUpdate1,
    version = version1,
    installedFromGooglePlay = installedFromGooglePlay1)

  val collectionId1 = 1

  val collectionId2 = 2

  val collection1 = ServicesCollection(
    id = collectionId1,
    position = position,
    name = name,
    collectionType = collectionType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    cards = Seq.empty,
    moment = None,
    appsCategory = Option(appsCategory.name),
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  val momentTimeSlot = MomentTimeSlot(
    from = "8:00",
    to = "19:00",
    days = Seq(0, 1, 1, 1, 1, 1, 0))

  def createSeqFormedWidgets(
    num: Int = 5,
    packageName: String = packageName,
    className: String = className,
    startX: Int = startX,
    startY: Int = startX,
    spanX: Int = startX,
    spanY: Int = startX,
    widgetType: WidgetType = AppWidgetType,
    label: Option[String] = None,
    imagePath: Option[String] = None,
    intent: Option[String] = None) =
    (0 until 5) map (
      item =>
        FormedWidget(
          packageName = packageName + item,
          className = className + item,
          startX = startX + item,
          startY = startY + item,
          spanX = spanX + item,
          spanY = spanY + item,
          widgetType = widgetType,
          label = label,
          imagePath = imagePath,
          intent = intent))

  val seqFormedWidgets = createSeqFormedWidgets()

  val formedMoment = FormedMoment(
    collectionId = Option(collectionId1),
    timeslot = Seq(momentTimeSlot),
    wifi = Seq.empty,
    headphone = false,
    momentType = Option(HomeMorningMoment),
    widgets = Option(seqFormedWidgets))

  def createSeqCollection(
    num: Int = 5,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: NineCardCategory = appsCategory,
    originalSharedCollectionId: Option[String] = originalSharedCollectionIdOption,
    sharedCollectionId: Option[String] = sharedCollectionIdOption,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    cards: Seq[Card] = seqCard,
    publicCollectionStatus: PublicCollectionStatus = publicCollectionStatus) =
    (0 until 5) map (
      item =>
        Collection(
          id = id + item,
          position = position,
          name = name,
          collectionType = collectionType,
          icon = icon,
          themedColorIndex = themedColorIndex,
          appsCategory = Option(appsCategory),
          originalSharedCollectionId = originalSharedCollectionId,
          sharedCollectionId = sharedCollectionId,
          sharedCollectionSubscribed = sharedCollectionSubscribed,
          cards = cards,
          publicCollectionStatus = publicCollectionStatus))

  def createSeqServicesCollection(
    num: Int = 5,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: NineCardCategory = appsCategory,
    originalSharedCollectionId: Option[String] = originalSharedCollectionIdOption,
    sharedCollectionId: Option[String] = sharedCollectionIdOption,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed) =
    (0 until 5) map (item =>
      ServicesCollection(
        id = id + item,
        position = position,
        name = name,
        collectionType = collectionType.name,
        icon = icon,
        themedColorIndex = themedColorIndex,
        appsCategory = Option(appsCategory.name),
        cards = Seq.empty,
        moment = None,
        originalSharedCollectionId = originalSharedCollectionId,
        sharedCollectionId = sharedCollectionId,
        sharedCollectionSubscribed = sharedCollectionSubscribed))

  def createSeqCard(
    num: Int = 5,
    id: Int = cardId,
    position: Int = position,
    term: String = term,
    packageName: String = generatePackageName,
    cardType: CardType = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification) =
    (0 until 5) map (item =>
      Card(
        id = id + item,
        position = position,
        term = term,
        packageName = Option(packageName),
        cardType = cardType,
        intent = Json.parse(intent).as[NineCardIntent],
        imagePath = Option(imagePath),
        notification = Option(notification)))

  def createSeqServicesCard() =
    (1 until 5) map (item =>
      ServicesCard(
        id = cardId + item,
        position = position + item,
        term = term,
        packageName = Option(packageName + item),
        cardType = cardType.name,
        intent = intent,
        imagePath = Option(imagePath),
        notification = Option(notification)))

  def createSeqUnformedApps(num: Int = 150) =
    (0 until num) map { item =>
      UnformedApp(
        name = name,
        packageName = generatePackageName,
        className = className,
        category = appsCategory)
    }

  def createSeqUnformedContacs(num: Int = 15) =
    (0 until num) map { item =>
      UnformedContact(
        name = name,
        lookupKey = lookupKey,
        photoUri = photoUri,
        info = Option(ContactInfo(Seq.empty, Seq(ContactPhone(phoneNumber, PhoneHome.toString)))))
    }

  val seqCardIds = (0 until 5) map (item => cardId + item)
  val seqCard = createSeqCard()
  val servicesCard = ServicesCard(
    id = cardId,
    position = position,
    term = term,
    packageName = Option(packageName),
    cardType = cardType.name,
    intent = intent,
    imagePath = Option(imagePath),
    notification = Option(notification))
  val seqServicesCard = Seq(servicesCard) ++ createSeqServicesCard()

  val seqProcessCard: Seq[Card] = seqServicesCard map {
    case (card) =>
      Card(
        id = card.id,
        position = card.position,
        term = card.term,
        packageName = card.packageName,
        cardType = CardType(card.cardType),
        intent = Json.parse(card.intent).as[NineCardIntent],
        imagePath = card.imagePath,
        notification = card.notification)
  }

  val seqCardPositions = (0 until seqProcessCard.size) map (item => 0 + item)
  val seqProcessCardReload = ServicesUpdateCardsRequest(createSeqServicesUpdateCardsRequest(seqProcessCard = seqProcessCard))

  def createSeqServicesUpdateCardsRequest(seqProcessCard: Seq[Card]): Seq[ServicesUpdateCardRequest] =
    seqProcessCard.zip(seqCardPositions) map {
      case (card, position) => ServicesUpdateCardRequest(
        id = card.id,
        position = position,
        term = card.term,
        packageName = card.packageName,
        cardType = card.cardType.name,
        intent = Json.toJson(card.intent).toString(),
        imagePath = card.imagePath,
        notification = card.notification)
    }


  val seqServicesApp = seqServicesCard map { card =>
    ServicesApp(
      id = card.id,
      name = card.term,
      packageName = card.packageName.getOrElse(""),
      className = "",
      category = appsCategoryName,
      dateInstalled = 0,
      dateUpdate = 0,
      version = "",
      installedFromGooglePlay = false)
  }

  val seqAddCardRequest = seqServicesCard map { c =>
    AddCardRequest(
      term = c.term,
      packageName = c.packageName,
      cardType = CardType(c.cardType),
      intent = Json.parse(c.intent).as[NineCardIntent],
      imagePath = c.imagePath)
  }

  val categorizedDetailPackages = seqServicesApp map { app =>
    CategorizedDetailPackage(
      packageName = app.packageName,
      title = app.name,
      category = Some(app.category),
      icon = "",
      free = true,
      downloads = "",
      stars = 0.0)
  }

  val seqCollection = createSeqCollection()
  val collection = seqCollection.headOption
  val seqServicesCollection = createSeqServicesCollection()
  val servicesCollection = seqServicesCollection.headOption

  val unformedApps = createSeqUnformedApps()
  val unformedContacts = createSeqUnformedContacs()

  val categoriesUnformedApps: Seq[NineCardCategory] = allCategories flatMap { category =>
    val count = unformedApps.count(_.category == category)
    if (count >= minAppsToAdd) Option(category) else None
  }

  val categoriesUnformedItems: Seq[NineCardCategory] = {
    val count = unformedContacts.size
    if (count >= minAppsToAdd) categoriesUnformedApps :+ ContactsCategory else categoriesUnformedApps
  }

  val collectionForUnformedItem = ServicesCollection(
    id = position,
    position = position,
    name = name,
    collectionType = collectionType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory.name),
    cards = Seq.empty,
    moment = None,
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  def createSeqFormedCollection(num: Int = 150) =
    (0 until num) map { item =>
      FormedCollection(
        name = name,
        originalSharedCollectionId = originalSharedCollectionIdOption,
        sharedCollectionId = sharedCollectionIdOption,
        sharedCollectionSubscribed = Option(sharedCollectionSubscribed),
        items = Seq.empty,
        collectionType = collectionType,
        icon = icon,
        category = Option(appsCategory),
        moment = Option(formedMoment))
    }

  val seqFormedCollection = createSeqFormedCollection()

  def createSeqServicesContact(num: Int = 10) =
    (0 until num) map { item =>
      ServicesContact(
        name = name,
        lookupKey = lookupKey,
        photoUri = photoUri,
        favorite = true)
    }

  val seqContacts: Seq[ServicesContact] = createSeqServicesContact()

  val seqContactsWithPhones: Seq[ServicesContact] = seqContacts map {
    _.copy(info = Option(ServicesContactInfo(Seq.empty, Seq(ServicesContactPhone(phoneNumber, PhoneHome)))))
  }

  val addCollectionRequest = AddCollectionRequest(
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    cards = Seq.empty,
    moment = None)

  val servicesCollectionAdded = ServicesCollection(
    id = seqServicesCollection.size,
    position = seqServicesCollection.size,
    name = name,
    collectionType = collectionType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryName),
    cards = Seq.empty,
    moment = None,
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribed)

  val collectionAdded = Collection(
    id = seqServicesCollection.size,
    position = seqServicesCollection.size,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribed,
    publicCollectionStatus = publicCollectionStatus)

  val editCollectionRequest = EditCollectionRequest(
    name = name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory))

  val editedCollection = Collection(
    id = collectionId,
    position = position,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = sharedCollectionIdOption,
    sharedCollectionSubscribed = sharedCollectionSubscribed,
    publicCollectionStatus = publicCollectionStatus)

  val updatedCollection = Collection(
    id = collectionId,
    position = position,
    name = name,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    originalSharedCollectionId = originalSharedCollectionIdOption,
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribed,
    publicCollectionStatus = publicCollectionStatus)

  val seqAddCardResponse = createSeqCardResponse()

  def createSeqAddCardRequest(num: Int = 3) =
    (0 until num) map { item =>
      AddCardRequest(
        term = term,
        packageName = Option(packageName),
        cardType = cardType,
        intent = Json.parse(intent).as[NineCardIntent],
        imagePath = Option(imagePath))
    }

  def createSeqCardResponse(
    num: Int = 3,
    id: Int = cardId,
    position: Int = position,
    term: String = term,
    packageName: String = packageName,
    cardType: CardType = cardType,
    intent: String = intent,
    imagePath: String = imagePath,
    notification: String = notification) =
    (0 until 3) map (item =>
      Card(
        id = id,
        position = position,
        term = term,
        packageName = Option(packageName),
        cardType = cardType,
        intent = Json.parse(intent).as[NineCardIntent],
        imagePath = Option(imagePath),
        notification = Option(notification)))

  def updatedCard = Card(
    id = servicesCard.id,
    position = servicesCard.position,
    term = servicesCard.term,
    packageName = servicesCard.packageName,
    cardType = cardType,
    intent = Json.parse(servicesCard.intent).as[NineCardIntent],
    imagePath = servicesCard.imagePath,
    notification = servicesCard.notification)

  val seqCategoryAndPackages =
    (seqServicesApp map (app => (app.category, app.packageName))).groupBy(_._1).mapValues(_.map(_._2)).toSeq

  def generateRankAppsResponse() = seqCategoryAndPackages map { item =>
    RankAppsResponse(
      category = item._1,
      packages = item._2)
  }

  val rankAppsResponseList = RankAppsResponseList(
    statusCode = statusCodeOk,
    items = generateRankAppsResponse())

  val packagesByCategory =
    seqCategoryAndPackages map { item =>
      PackagesByCategory(
        category = item._1,
        packages = item._2)
    }

  val awarenessLocation =
    AwarenessLocation(
      latitude = latitude,
      longitude = longitude,
      countryCode = Some("ES"),
      countryName = Some("Spain"),
      addressLines = Seq("street", "city", "postal code")
    )


  val seqUnformedAppsForPrivateCollections: Seq[UnformedApp] =
    Seq(
      UnformedApp(
        name = "nameUnformed0",
        packageName = "package.name.0",
        className = "classNameUnformed0",
        category = appsCategories(0)),
      UnformedApp(
        name = "nameUnformed1",
        packageName = "package.name.1",
        className = "classNameUnformed1",
        category = appsCategories(1)))

  val appsByCategory0: Seq[UnformedApp] = seqUnformedAppsForPrivateCollections.filter(_.category.toAppCategory == appsCategories(0)).take(numSpaces)
  val appsByCategory1: Seq[UnformedApp] = seqUnformedAppsForPrivateCollections.filter(_.category.toAppCategory == appsCategories(1)).take(numSpaces)

  val collectionProcessConfig: CollectionProcessConfig

  val seqPrivateCollection =
    Seq(
      PrivateCollection(
        name = appsCategories(0).getStringResource,
        collectionType = AppsCollectionType,
        icon = appsCategories(0).getStringResource,
        themedColorIndex = 0,
        appsCategory = Some(appsCategories(0)),
        cards = Seq(
          PrivateCard(
            term = "nameUnformed0",
            packageName = Some("package.name.0"),
            cardType = AppCardType,
            intent = NineCardIntent(NineCardIntentExtras(
              package_name = Option("package.name.0"),
              class_name = Option("classNameUnformed0"))),
            imagePath = Some("imagePathUnformed0")
          )),
        moment = None),
      PrivateCollection(
        name = appsCategories(1).getStringResource,
        collectionType = AppsCollectionType,
        icon = appsCategories(1).getStringResource,
        themedColorIndex = 1,
        appsCategory = Some(appsCategories(1)),
        cards = Seq(
          PrivateCard(
            term = "nameUnformed1",
            packageName = Some("package.name.1"),
            cardType = AppCardType,
            intent = NineCardIntent(NineCardIntentExtras(
              package_name = Option("package.name.1"),
              class_name = Option("classNameUnformed1"))),
            imagePath = Some("imagePathUnformed1")
          )),
        moment = None)
    )

}
