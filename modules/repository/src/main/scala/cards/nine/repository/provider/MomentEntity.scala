package cards.nine.repository.provider

import android.database.Cursor
import cards.nine.repository.model.Moment
import cards.nine.repository.Conversions._

case class MomentEntity(id: Int, data: MomentEntityData)

case class MomentEntityData(
  collectionId: Option[Int],
  timeslot: String,
  wifi: String,
  headphone: Boolean,
  momentType : String)

object MomentEntity {
  val table = "Moment"
  val collectionId = "collectionId"
  val timeslot = "timeslot"
  val wifi = "wifi"
  val headphone = "headphone"
  val momentType = "momentType"

  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    collectionId,
    timeslot,
    wifi,
    headphone,
    momentType)

  def momentEntityFromCursor(cursor: Cursor): MomentEntity = {
    val collectionIdColumn = cursor.getColumnIndex(collectionId)
    MomentEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = MomentEntityData(
        collectionId = if (cursor.isNull(collectionIdColumn)) None else Some(cursor.getInt(collectionIdColumn)),
        timeslot = cursor.getString(cursor.getColumnIndex(timeslot)),
        wifi = cursor.getString(cursor.getColumnIndex(wifi)),
        headphone = cursor.getInt(cursor.getColumnIndex(headphone)) > 0,
        momentType = cursor.getString(cursor.getColumnIndex(momentType))))
  }


  def momentFromCursor(cursor: Cursor): Moment = toMoment(momentEntityFromCursor(cursor))

  def createTableSQL: String =
    s"""CREATE TABLE ${MomentEntity.table}
        |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
        |${MomentEntity.collectionId} INTEGER,
        |${MomentEntity.timeslot} TEXT not null,
        |${MomentEntity.wifi} TEXT not null,
        |${MomentEntity.headphone} INTEGER not null,
        |${MomentEntity.momentType} TEXT)""".stripMargin
}
