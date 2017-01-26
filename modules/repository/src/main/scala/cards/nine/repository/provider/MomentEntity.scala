/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.repository.provider

import android.database.Cursor
import cards.nine.repository.model.Moment
import cards.nine.repository.Conversions._

case class MomentEntity(id: Int, data: MomentEntityData)

case class MomentEntityData(
    collectionId: Option[Int],
    timeslot: String,
    wifi: String,
    bluetooth: String,
    headphone: Boolean,
    momentType: String)

object MomentEntity {
  val table        = "Moment"
  val collectionId = "collectionId"
  val timeslot     = "timeslot"
  val wifi         = "wifi"
  val bluetooth    = "bluetooth"
  val headphone    = "headphone"
  val momentType   = "momentType"

  val allFields =
    Seq[String](
      NineCardsSqlHelper.id,
      collectionId,
      timeslot,
      wifi,
      bluetooth,
      headphone,
      momentType)

  def momentEntityFromCursor(cursor: Cursor): MomentEntity = {
    val collectionIdColumn = cursor.getColumnIndex(collectionId)
    val bluetoothColumn    = cursor.getColumnIndex(bluetooth)
    MomentEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = MomentEntityData(
        collectionId =
          if (cursor.isNull(collectionIdColumn)) None
          else Option(cursor.getInt(collectionIdColumn)),
        timeslot = cursor.getString(cursor.getColumnIndex(timeslot)),
        wifi = cursor.getString(cursor.getColumnIndex(wifi)),
        bluetooth = if (cursor.isNull(bluetoothColumn)) "" else cursor.getString(bluetoothColumn),
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
        |${MomentEntity.momentType} TEXT,
        |${MomentEntity.bluetooth} TEXT)""".stripMargin
}
