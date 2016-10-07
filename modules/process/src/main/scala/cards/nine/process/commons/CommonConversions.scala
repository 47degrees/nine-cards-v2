package cards.nine.process.commons

import cards.nine.models.types._
import cards.nine.process.collection.models.FormedMoment
import cards.nine.process.commons.models.{Moment, MomentTimeSlot, _}
import cards.nine.process.moment.{SaveMomentRequest, UpdateMomentRequest}
import cards.nine.services.persistence.models.{Card => ServicesCard, Collection => ServicesCollection, Moment => ServicesMoment, MomentTimeSlot => ServicesMomentTimeSlot}
import cards.nine.services.persistence.{AddMomentRequest, SaveWidgetRequest => ServiceSaveWidgetRequest, UpdateMomentRequest => ServiceUpdateMomentRequest}

trait CommonConversions extends NineCardIntentConversions {

  def toCollection(servicesCollection: ServicesCollection): Collection = Collection(
    id = servicesCollection.id,
    position = servicesCollection.position,
    name = servicesCollection.name,
    collectionType = CollectionType(servicesCollection.collectionType),
    icon = servicesCollection.icon,
    themedColorIndex = servicesCollection.themedColorIndex,
    appsCategory = servicesCollection.appsCategory map (NineCardCategory(_)),
    originalSharedCollectionId = servicesCollection.originalSharedCollectionId,
    sharedCollectionId = servicesCollection.sharedCollectionId,
    sharedCollectionSubscribed = servicesCollection.sharedCollectionSubscribed,
    cards = servicesCollection.cards map toCard,
    moment = servicesCollection.moment map toMoment,
    publicCollectionStatus = determinePublicCollectionStatus(Some(servicesCollection)))

  def toCard(servicesCard: ServicesCard): Card = Card(
    id = servicesCard.id,
    position = servicesCard.position,
    term = servicesCard.term,
    packageName = servicesCard.packageName,
    cardType = CardType(servicesCard.cardType),
    intent = jsonToNineCardIntent(servicesCard.intent),
    imagePath = servicesCard.imagePath,
    notification = servicesCard.notification)

  def toMoment(servicesMoment: ServicesMoment): Moment = Moment(
    id = servicesMoment.id,
    collectionId = servicesMoment.collectionId,
    timeslot = servicesMoment.timeslot map toTimeSlot,
    wifi = servicesMoment.wifi,
    headphone = servicesMoment.headphone,
    momentType = servicesMoment.momentType map (NineCardsMoment(_)))

  def toTimeSlot(servicesMomentTimeSlot:  ServicesMomentTimeSlot): MomentTimeSlot = MomentTimeSlot(
    from = servicesMomentTimeSlot.from,
    to = servicesMomentTimeSlot.to,
    days = servicesMomentTimeSlot.days)

  def toAddMomentRequest(moment: SaveMomentRequest): AddMomentRequest =
    AddMomentRequest(
      collectionId = moment.collectionId,
      timeslot = moment.timeslot map toServicesMomentTimeSlot,
      wifi = moment.wifi,
      headphone = moment.headphone,
      momentType = moment.momentType map (_.name),
      widgets = moment.widgets getOrElse Seq.empty map toServiceSaveWidgetRequest)

  def toAddMomentRequest(moment: FormedMoment): AddMomentRequest =
    AddMomentRequest(
      collectionId = moment.collectionId,
      timeslot = moment.timeslot map toServicesMomentTimeSlot,
      wifi = moment.wifi,
      headphone = moment.headphone,
      momentType = moment.momentType map (_.name),
      widgets = moment.widgets getOrElse Seq.empty map toServiceSaveWidgetRequest)

  def toAddMomentRequest(collectionId: Option[Int], moment: NineCardsMoment): AddMomentRequest = {

    def toServicesMomentTimeSlotSeq(moment: NineCardsMoment): Seq[ServicesMomentTimeSlot] =
      moment match {
        case HomeMorningMoment => Seq(ServicesMomentTimeSlot(from = "08:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
        case WorkMoment => Seq(ServicesMomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
        case HomeNightMoment => Seq(ServicesMomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)), ServicesMomentTimeSlot(from = "00:00", to = "08:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
        case StudyMoment => Seq(ServicesMomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
        case MusicMoment => Seq.empty
        case CarMoment => Seq.empty
        case RunningMoment => Seq.empty
        case BikeMoment => Seq.empty
        case WalkMoment => Seq.empty
      }

    AddMomentRequest(
      collectionId = collectionId,
      timeslot = toServicesMomentTimeSlotSeq(moment),
      wifi = Seq.empty,
      headphone = moment == MusicMoment,
      momentType = Option(moment.name),
      widgets = Seq.empty)
  }

  def toServiceSaveWidgetRequest(widget: FormedWidget): ServiceSaveWidgetRequest =
    ServiceSaveWidgetRequest(
      packageName = widget.packageName,
      className = widget.className,
      appWidgetId = 0,
      startX = widget.startX,
      startY = widget.startY,
      spanX = widget.spanX,
      spanY = widget.spanY,
      widgetType = widget.widgetType.name,
      label = widget.label,
      imagePath = widget.imagePath,
      intent = widget.intent)

  def toServiceUpdateMomentRequest(moment: UpdateMomentRequest): ServiceUpdateMomentRequest =
    ServiceUpdateMomentRequest(
      id = moment.id,
      collectionId = moment.collectionId,
      timeslot = moment.timeslot map toServicesMomentTimeSlot,
      wifi = moment.wifi,
      headphone = moment.headphone,
      momentType = moment.momentType map (_.name))

  def toServicesMomentTimeSlot(timeSlot: MomentTimeSlot): ServicesMomentTimeSlot =
    ServicesMomentTimeSlot(
      from = timeSlot.from,
      to = timeSlot.to,
      days = timeSlot.days)

  def determinePublicCollectionStatus(maybeCollection: Option[ServicesCollection]): PublicCollectionStatus =
    maybeCollection match {
      case Some(c) if c.sharedCollectionId.isDefined && c.sharedCollectionSubscribed => Subscribed
      case Some(c) if c.sharedCollectionId.isDefined && c.originalSharedCollectionId == c.sharedCollectionId =>
        PublishedByOther
      case Some(c) if c.sharedCollectionId.isDefined =>
        PublishedByMe
      case _ => NotPublished
    }

}
