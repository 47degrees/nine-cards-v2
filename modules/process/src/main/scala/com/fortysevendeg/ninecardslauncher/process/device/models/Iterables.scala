package com.fortysevendeg.ninecardslauncher.process.device.models

import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor
import com.fortysevendeg.ninecardslauncher.process.commons.types.Misc
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.device.DeviceConversions
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact => ServicesContact}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{IterableApps => ServicesIterableApps}

class IterableApps(cursor: ServicesIterableApps)
  extends IterableCursor[App]
  with DeviceConversions {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): App = toApp(cursor.moveToPosition(pos))

  override def close(): Unit = cursor.close()

}

class EmptyIterableApps()
  extends IterableApps(javaNull) {
  val emptyApp = App("", "", "", Misc, "", 0, 0, "", installedFromGooglePlay = false)
  override def count(): Int = 0
  override def moveToPosition(pos: Int): App = emptyApp
  override def close(): Unit = {}
}


class IterableContacts(cursor: IterableCursor[ServicesContact])
  extends IterableCursor[Contact]
    with DeviceConversions {

  override def count(): Int = cursor.count()

  override def moveToPosition(pos: Int): Contact = toContact(cursor.moveToPosition(pos))

  override def close(): Unit = cursor.close()

}
