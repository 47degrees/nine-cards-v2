package com.fortysevendeg.ninecardslauncher.process.types

import com.fortysevendeg.ninecardslauncher.process.commons.CollectionTypes._

sealed trait CollectionType {
  val name: String
}

case object AppsCollectionType extends CollectionType {
  override val name: String = apps
}

case object ContactsCollectionType extends CollectionType {
  override val name: String = contacts
}

case object HomeMorningCollectionType extends CollectionType {
  override val name: String = homeMorning
}

case object HomeNightCollectionType extends CollectionType {
  override val name: String = homeNight
}

case object WorkCollectionType extends CollectionType {
  override val name: String = work
}

case object TransitCollectionType extends CollectionType {
  override val name: String = transit
}

case object FreeCollectionType extends CollectionType {
  override val name: String = free
}

object CollectionType {

  val cases = Seq(AppsCollectionType, ContactsCollectionType, HomeMorningCollectionType, HomeNightCollectionType, WorkCollectionType, TransitCollectionType, FreeCollectionType)

  def apply(name: String): CollectionType = cases find (_.name == name) getOrElse
    (throw new IllegalArgumentException(s"$name not found"))

}



