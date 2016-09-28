package cards.nine.process.commons.types

sealed trait PublicCollectionStatus

case object NotPublished extends PublicCollectionStatus

case object PublishedByMe extends PublicCollectionStatus

case object PublishedByOther extends PublicCollectionStatus

case object Subscribed extends PublicCollectionStatus