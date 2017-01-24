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

package cards.nine.models

import java.util.Date

import cards.nine.models.types._

trait CloudStorageResource {
  def cloudId: String
  def deviceId: Option[String]
  def deviceName: String
  def createdDate: Date
  def modifiedDate: Date
}

case class CloudStorageDeviceSummary(
    cloudId: String,
    deviceId: Option[String],
    deviceName: String,
    createdDate: Date,
    modifiedDate: Date,
    currentDevice: Boolean)
    extends CloudStorageResource

case class CloudStorageDevice(
    cloudId: String,
    createdDate: Date,
    modifiedDate: Date,
    data: CloudStorageDeviceData)
    extends CloudStorageResource {

  override def deviceId: Option[String] = Some(data.deviceId)

  override def deviceName: String = data.deviceName
}

case class RawCloudStorageDevice(
    cloudId: String,
    uuid: String,
    title: String,
    deviceId: Option[String],
    createdDate: Date,
    modifiedDate: Date,
    json: String)

case class CloudStorageDeviceData(
    deviceId: String,
    deviceName: String,
    documentVersion: Int,
    collections: Seq[CloudStorageCollection],
    moments: Option[Seq[CloudStorageMoment]],
    dockApps: Option[Seq[CloudStorageDockApp]])

case class CloudStorageCollection(
    name: String,
    originalSharedCollectionId: Option[String],
    sharedCollectionId: Option[String],
    sharedCollectionSubscribed: Option[Boolean],
    items: Seq[CloudStorageCollectionItem],
    collectionType: CollectionType,
    icon: String,
    category: Option[NineCardsCategory],
    moment: Option[CloudStorageMoment])

case class CloudStorageCollectionItem(itemType: String, title: String, intent: String)

case class CloudStorageMoment(
    timeslot: Seq[CloudStorageMomentTimeSlot],
    wifi: Seq[String],
    bluetooth: Option[Seq[String]],
    headphones: Boolean,
    momentType: NineCardsMoment,
    widgets: Option[Seq[CloudStorageWidget]])

case class CloudStorageMomentTimeSlot(from: String, to: String, days: Seq[Int])

case class CloudStorageWidget(
    packageName: String,
    className: String,
    area: CloudStorageWidgetArea,
    widgetType: WidgetType,
    label: Option[String],
    imagePath: Option[String],
    intent: Option[String])

case class CloudStorageWidgetArea(startX: Int, startY: Int, spanX: Int, spanY: Int)

case class CloudStorageDockApp(
    name: String,
    dockType: DockType,
    intent: String,
    imagePath: String,
    position: Int)
