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

package cards.nine.app.ui.collections.jobs

import android.content.Intent
import cards.nine.app.commons.{AppNineCardsIntentConversions, Conversions}
import cards.nine.app.ui.collections.CollectionsDetailsActivity._
import cards.nine.app.ui.collections.jobs.uiactions.{
  GroupCollectionsUiActions,
  NavigationUiActions,
  ToolbarUiActions
}
import cards.nine.app.ui.commons.action_filters.MomentReloadedActionFilter
import cards.nine.app.ui.commons._
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.Card._
import cards.nine.models.types._
import cards.nine.models.{Card, CardData, Collection}
import cats.implicits._
import macroid.ActivityContextWrapper

class GroupCollectionsJobs(
    val groupCollectionsUiActions: GroupCollectionsUiActions,
    val toolbarUiActions: ToolbarUiActions,
    val navigationUiActions: NavigationUiActions)(
    implicit activityContextWrapper: ActivityContextWrapper)
    extends ShortcutJobs
    with Conversions
    with AppNineCardsIntentConversions { self =>

  val delay = 200

  var collections: Seq[Collection] = Seq.empty

  def initialize(
      backgroundColor: Int,
      initialToolbarColor: Int,
      icon: String,
      position: Int,
      isStateChanged: Boolean): TaskService[Unit] = {
    for {
      _           <- toolbarUiActions.initialize(backgroundColor, initialToolbarColor, icon, isStateChanged)
      theme       <- getThemeTask
      _           <- TaskService.right(statuses = statuses.copy(theme = theme))
      _           <- groupCollectionsUiActions.initialize()
      collections <- di.collectionProcess.getCollections
      _           <- groupCollectionsUiActions.showCollections(collections, position)
    } yield ()
  }

  def resume(): TaskService[Unit] = di.observerRegister.registerObserverTask()

  def pause(): TaskService[Unit] = di.observerRegister.unregisterObserverTask()

  def back(): TaskService[Unit] = groupCollectionsUiActions.back()

  def destroy(): TaskService[Unit] = groupCollectionsUiActions.destroy()

  def reloadCards(): TaskService[Seq[Card]] =
    for {
      currentCollection <- fetchCurrentCollection
      databaseCollection <- di.collectionProcess
        .getCollectionById(currentCollection.id)
        .resolveOption(s"Can't find the collection with id ${currentCollection.id}")
      cardsAreDifferent = databaseCollection.cards != currentCollection.cards
      _ <- groupCollectionsUiActions
        .reloadCards(databaseCollection.cards)
        .resolveIf(cardsAreDifferent, ())
      currentIsMoment <- collectionIsMoment(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action))
        .resolveIf(cardsAreDifferent && currentIsMoment, ())
    } yield databaseCollection.cards

  def editCard(): TaskService[Unit] =
    for {
      currentCollection <- fetchCurrentCollection
      currentCollectionId = currentCollection.id
      cards               = filterSelectedCards(currentCollection.cards)
      _ <- cards match {
        case head :: tail if tail.isEmpty =>
          closeEditingMode() *> groupCollectionsUiActions
            .editCard(currentCollectionId, head.id, head.term)
        case _ =>
          TaskService.left[Unit](JobException("You only can edit one card"))
      }
    } yield ()

  def removeCardsInEditMode(): TaskService[Seq[Card]] =
    for {
      currentCollection <- fetchCurrentCollection
      cards = filterSelectedCards(currentCollection.cards)
      _ <- closeEditingMode()
      _ <- removeCards(currentCollection.id, cards)
    } yield cards

  def removeCardsByPackagesName(packageNames: Seq[String]): TaskService[Seq[Card]] =
    for {
      _                 <- di.trackEventProcess.removeAppsByFab(packageNames)
      currentCollection <- fetchCurrentCollection
      cards = packageNames flatMap (packageName =>
                                      currentCollection.cards.find(
                                        _.packageName == Option(packageName)))
      _ <- removeCards(currentCollection.id, cards)
    } yield cards

  def removeCards(currentCollectionId: Int, cards: Seq[Card]) =
    for {
      _               <- di.trackEventProcess.removeApplications(cards flatMap (_.packageName))
      _               <- di.collectionProcess.deleteCards(currentCollectionId, cards map (_.id))
      _               <- groupCollectionsUiActions.removeCards(cards)
      currentIsMoment <- collectionIsMoment(currentCollectionId)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action))
        .resolveIf(currentIsMoment, ())
    } yield cards

  def moveToCollection(toCollectionId: Int, collectionPosition: Int): TaskService[Seq[Card]] =
    for {
      currentCollection <- fetchCurrentCollection
      _                 <- di.trackEventProcess.moveApplications(currentCollection.name)
      toCollection <- groupCollectionsUiActions
        .getCollection(collectionPosition)
        .resolveOption(s"Can't find the collection in the position $collectionPosition in the UI")
      currentCollectionId = currentCollection.id
      cards               = filterSelectedCards(currentCollection.cards)
      otherIsMoment       = toCollection.collectionType == MomentCollectionType
      _               <- closeEditingMode()
      _               <- di.collectionProcess.deleteCards(currentCollectionId, cards map (_.id))
      _               <- di.collectionProcess.addCards(toCollectionId, cards map (_.toData))
      _               <- groupCollectionsUiActions.removeCards(cards)
      _               <- groupCollectionsUiActions.addCardsToCollection(collectionPosition, cards)
      currentIsMoment <- collectionIsMoment(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action))
        .resolveIf(currentIsMoment || otherIsMoment, ())
    } yield cards

  def savePublishStatus(): TaskService[Unit] =
    for {
      currentCollection <- fetchCurrentCollection
      _ <- TaskService.right(
        statuses = statuses.copy(publishStatus = currentCollection.publicCollectionStatus))
    } yield ()

  def performCard(card: Card, position: Int): TaskService[Unit] = {

    def sendTrackEvent() = {
      val packageName     = card.packageName getOrElse ""
      val maybeCollection = groupCollectionsUiActions.dom.getCurrentCollection

      def trackMomentIfNecessary(collectionId: Option[Int]) =
        collectionId match {
          case Some(id) =>
            for {
              maybeMoment <- di.momentProcess.getMomentByCollectionId(id)
              _ <- maybeMoment match {
                case Some(moment) =>
                  di.trackEventProcess
                    .openAppFromCollection(packageName, MomentCategory(moment.momentType))
                case _ => TaskService.empty
              }
            } yield ()
          case _ => TaskService.empty
        }

      for {
        _ <- maybeCollection flatMap (_.appsCategory) match {
          case Some(category) =>
            di.trackEventProcess.openAppFromCollection(packageName, AppCategory(category))
          case _ => TaskService.empty
        }
        _ <- trackMomentIfNecessary(maybeCollection.map(_.id))
      } yield ()
    }

    statuses.collectionMode match {
      case EditingCollectionMode =>
        val positions = if (statuses.positionsEditing.contains(position)) {
          statuses.positionsEditing - position
        } else {
          statuses.positionsEditing + position
        }
        statuses = statuses.copy(positionsEditing = positions)
        if (statuses.positionsEditing.isEmpty) {
          closeEditingMode()
        } else {
          groupCollectionsUiActions.reloadItemCollection(statuses.getPositionsSelected, position)
        }
      case NormalCollectionMode =>
        di.launcherExecutorProcess.execute(card.intent) *> sendTrackEvent()

    }
  }

  def requestCallPhonePermission(phone: Option[String]): TaskService[Unit] = {
    statuses = statuses.copy(lastPhone = phone)
    di.userAccountsProcess.requestPermission(RequestCodes.phoneCallPermission, CallPhone)
  }

  def requestPermissionsResult(
      requestCode: Int,
      permissions: Array[String],
      grantResults: Array[Int]): TaskService[Unit] =
    if (requestCode == RequestCodes.phoneCallPermission) {
      for {
        result <- di.userAccountsProcess.parsePermissionsRequestResult(permissions, grantResults)
        hasCallPhonePermission = result.exists(_.hasPermission(CallPhone))
        _ <- (hasCallPhonePermission, statuses.lastPhone) match {
          case (true, Some(phone)) =>
            di.launcherExecutorProcess.execute(phoneToNineCardIntent(None, phone))
          case (false, Some(phone)) =>
            di.launcherExecutorProcess.launchDial(Some(phone)) *>
              groupCollectionsUiActions.showNoPhoneCallPermissionError()
          case _ => TaskService.empty
        }
        _ <- TaskService.right(statuses = statuses.copy(lastPhone = None))
      } yield ()
    } else {
      TaskService.empty
    }

  def addCards(cardsRequest: Seq[CardData]): TaskService[Seq[Card]] =
    for {
      _                 <- di.trackEventProcess.addAppsByFab(cardsRequest flatMap (_.packageName))
      currentCollection <- fetchCurrentCollection
      currentCollectionId = currentCollection.id
      cards           <- di.collectionProcess.addCards(currentCollectionId, cardsRequest)
      _               <- groupCollectionsUiActions.addCards(cards)
      currentIsMoment <- collectionIsMoment(currentCollection.id)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action))
        .resolveIf(currentIsMoment, ())
    } yield cards

  def addShortcut(intent: Intent): TaskService[Option[Card]] = {

    def shortcutAdded(collectionId: Int, card: Card): TaskService[Unit] =
      for {
        _               <- di.trackEventProcess.addShortcutByFab(card.term)
        _               <- groupCollectionsUiActions.addCards(Seq(card))
        currentIsMoment <- collectionIsMoment(collectionId)
        _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action))
          .resolveIf(currentIsMoment, ())
      } yield ()

    for {
      currentCollection <- fetchCurrentCollection
      maybeCard         <- addNewShortcut(currentCollection.id, intent)
      _ <- maybeCard match {
        case Some(card) => shortcutAdded(currentCollection.id, card)
        case _          => TaskService.empty
      }
    } yield maybeCard
  }

  def openReorderMode(): TaskService[Unit] =
    for {
      _ <- statuses.collectionMode match {
        case EditingCollectionMode =>
          groupCollectionsUiActions.closeEditingModeUi()
        case _ =>
          TaskService.right(statuses = statuses.copy(collectionMode = EditingCollectionMode))
      }
      _ <- groupCollectionsUiActions.openReorderModeUi()
    } yield ()

  def closeReorderMode(position: Int): TaskService[Unit] = {
    statuses = statuses.copy(positionsEditing = Set(position))
    groupCollectionsUiActions.startEditing(statuses.getPositionsSelected)
  }

  def closeEditingMode(): TaskService[Unit] = {
    statuses = statuses.copy(collectionMode = NormalCollectionMode, positionsEditing = Set.empty)
    groupCollectionsUiActions.closeEditingModeUi()
  }

  def emptyCollection(): TaskService[Unit] =
    for {
      currentCollection <- fetchCurrentCollection
      _ <- groupCollectionsUiActions
        .showMenu(autoHide = false, indexColor = currentCollection.themedColorIndex)
    } yield ()

  def firstItemInCollection(): TaskService[Unit] =
    groupCollectionsUiActions.hideMenuButton()

  def close(): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.closeCollectionByGesture()
      _ <- groupCollectionsUiActions.close()
    } yield ()

  def showMenu(openMenu: Boolean = false): TaskService[Unit] =
    for {
      _                 <- di.trackEventProcess.addCardByMenu()
      currentCollection <- fetchCurrentCollection
      _ <- groupCollectionsUiActions
        .showMenu(autoHide = true, openMenu = openMenu, currentCollection.themedColorIndex)
    } yield ()

  private[this] def filterSelectedCards(cards: Seq[Card]): Seq[Card] =
    cards.zipWithIndex flatMap {
      case (card, index) if statuses.positionsEditing.contains(index) =>
        Option(card)
      case _ => None
    }

  private[this] def fetchCurrentCollection: TaskService[Collection] =
    groupCollectionsUiActions.getCurrentCollection.resolveOption(
      "Can't find the current collection in the UI")

  private[this] def collectionIsMoment(currentCollectionId: Int): TaskService[Boolean] =
    for {
      // TODO Create getMomentByCollectionId #975
      moments <- di.momentProcess.getMoments
      currentIsMoment = moments.exists(_.collectionId.contains(currentCollectionId))
    } yield currentIsMoment

}

sealed trait CollectionMode

case object NormalCollectionMode extends CollectionMode

case object EditingCollectionMode extends CollectionMode
