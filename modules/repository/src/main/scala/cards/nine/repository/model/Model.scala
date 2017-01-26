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

package cards.nine.repository.model

case class App(id: Int, data: AppData)

case class AppData(
    name: String,
    packageName: String,
    className: String,
    category: String,
    dateInstalled: Long,
    dateUpdate: Long,
    version: String,
    installedFromGooglePlay: Boolean)

case class Collection(id: Int, data: CollectionData)

case class CollectionData(
    position: Int,
    name: String,
    collectionType: String,
    icon: String,
    themedColorIndex: Int,
    appsCategory: Option[String] = None,
    originalSharedCollectionId: Option[String] = None,
    sharedCollectionId: Option[String] = None,
    sharedCollectionSubscribed: Option[Boolean])

case class Card(id: Int, data: CardData)

case class CardsWithCollectionId(collectionId: Int, data: Seq[CardData])

case class CardData(
    position: Int,
    term: String,
    packageName: Option[String],
    cardType: String,
    intent: String,
    imagePath: Option[String],
    notification: Option[String] = None)

case class DockApp(id: Int, data: DockAppData)

case class DockAppData(
    name: String,
    dockType: String,
    intent: String,
    imagePath: String,
    position: Int)

case class Moment(id: Int, data: MomentData)

case class MomentData(
    collectionId: Option[Int],
    timeslot: String,
    wifi: String,
    bluetooth: String,
    headphone: Boolean,
    momentType: Option[String])

case class User(id: Int, data: UserData)

case class UserData(
    email: Option[String],
    apiKey: Option[String],
    sessionToken: Option[String],
    deviceToken: Option[String],
    marketToken: Option[String],
    name: Option[String],
    avatar: Option[String],
    cover: Option[String],
    deviceName: Option[String],
    deviceCloudId: Option[String])

case class Widget(id: Int, data: WidgetData)

case class WidgetData(
    momentId: Int,
    packageName: String,
    className: String,
    appWidgetId: Int,
    startX: Int,
    startY: Int,
    spanX: Int,
    spanY: Int,
    widgetType: String,
    label: Option[String],
    imagePath: Option[String],
    intent: Option[String])

case class DataCounter(term: String, count: Int)
