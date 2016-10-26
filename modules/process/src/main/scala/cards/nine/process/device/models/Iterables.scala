package cards.nine.process.device.models

import cards.nine.commons.contentresolver.IterableCursor
import cards.nine.commons.javaNull
import cards.nine.models
import cards.nine.models.{Contact, ApplicationData}
import cards.nine.models.types.Misc
import cards.nine.services.persistence.models.{IterableApps => ServicesIterableApps}

class IterableApps(cursor: ServicesIterableApps)
  extends IterableCursor[ApplicationData] {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): ApplicationData = cursor.moveToPosition(pos).toData

  override def close(): Unit = cursor.close()

}

class EmptyIterableApps()
  extends IterableApps(javaNull) {
  val emptyApp = ApplicationData("", "", "", Misc, 0, 0, "", installedFromGooglePlay = false)
  override def count(): Int = 0
  override def moveToPosition(pos: Int): ApplicationData = emptyApp
  override def close(): Unit = {}
}


class IterableContacts(cursor: IterableCursor[models.Contact])
  extends IterableCursor[Contact] {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): Contact = cursor.moveToPosition(pos)

  override def close(): Unit = cursor.close()

}
