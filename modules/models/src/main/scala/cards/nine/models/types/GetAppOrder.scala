package cards.nine.models.types

sealed trait GetAppOrder {
  val ascending: Boolean
}

case class GetByName(ascending: Boolean) extends GetAppOrder

object GetByName extends GetByName(true)

case class GetByInstallDate(ascending: Boolean) extends GetAppOrder

object GetByInstallDate extends GetByInstallDate(false)

case class GetByCategory(ascending: Boolean) extends GetAppOrder

object GetByCategory extends GetByCategory(true)