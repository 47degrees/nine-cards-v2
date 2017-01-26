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

import android.content.Context
import android.support.v7.widget.RecyclerView.ViewHolder
import cards.nine.app.commons.{AppNineCardsIntentConversions, Conversions}
import cards.nine.app.receivers.shortcuts.ShortcutBroadcastReceiver._
import cards.nine.app.ui.collections.jobs.uiactions.SingleCollectionUiActions
import cards.nine.app.ui.commons.{JobException, Jobs}
import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types._
import cards.nine.models.{Card, Collection}
import cats.syntax.either._
import macroid.ActivityContextWrapper
import monix.eval.Task

class SingleCollectionJobs(
    animateCards: Boolean,
    maybeCollection: Option[Collection],
    val actions: SingleCollectionUiActions)(
    implicit activityContextWrapper: ActivityContextWrapper)
    extends Jobs
    with Conversions
    with AppNineCardsIntentConversions { self =>

  lazy val preferences =
    contextSupport.context.getSharedPreferences(shortcutBroadcastPreferences, Context.MODE_PRIVATE)

  def initialize(): TaskService[Unit] = {
    for {
      theme <- getThemeTask
      _ <- maybeCollection match {
        case Some(collection) => actions.initialize(animateCards, collection)
        case _                => actions.showEmptyCollection()
      }
    } yield ()
  }

  def startReorderCards(holder: ViewHolder): TaskService[Unit] =
    for {
      pulling <- actions.isToolbarPulling
      _       <- actions.startReorder(holder).resolveIf(!pulling, ())
    } yield ()

  def reorderCard(collectionId: Int, cardId: Int, position: Int): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.reorderApplication(position)
      _ <- di.collectionProcess.reorderCard(collectionId, cardId, position)
      _ <- actions.reloadCards()
    } yield ()

  def moveToCollection(): TaskService[Unit] =
    for {
      collections <- di.collectionProcess.getCollections
      _           <- actions.moveToCollection(collections)
    } yield ()

  def addCards(cards: Seq[Card]): TaskService[Unit] =
    for {
      _ <- trackCards(cards, AddedToCollectionAction)
      _ <- actions.addCards(cards)
    } yield ()

  def removeCards(cards: Seq[Card]): TaskService[Unit] =
    for {
      _ <- trackCards(cards, RemovedFromCollectionAction)
      _ <- actions.removeCards(cards)
    } yield ()

  def reloadCards(cards: Seq[Card]): TaskService[Unit] =
    actions.reloadCards(cards)

  def bindAnimatedAdapter(): TaskService[Unit] = maybeCollection match {
    case Some(collection) =>
      actions.bindAnimatedAdapter(animateCards, collection)
    case _ => TaskService.left(JobException("Collection not found"))
  }

  def saveEditedCard(collectionId: Int, cardId: Int, cardName: Option[String]): TaskService[Unit] =
    cardName match {
      case Some(name) if name.length > 0 =>
        for {
          card <- di.collectionProcess.editCard(collectionId, cardId, name)
          _    <- actions.reloadCard(card)
        } yield ()
      case _ => actions.showMessageFormFieldError
    }

  def showData(): TaskService[Unit] = maybeCollection match {
    case Some(collection) => actions.showData(collection.cards.isEmpty)
    case _                => TaskService.left(JobException("Collection not found"))
  }

  def showGenericError(): TaskService[Unit] = actions.showContactUsError()

  def saveCollectionIdForShortcut(): TaskService[Unit] =
    for {
      collection <- actions.getCurrentCollection.resolveOption(
        "Can't find the current collection in the UI")
      _ <- TaskService(
        CatchAll[JobException](preferences.edit().putInt(collectionIdKey, collection.id).apply()))
    } yield ()

  def removeCollectionIdForShortcut(): TaskService[Unit] = TaskService {
    CatchAll[JobException](preferences.edit().remove(collectionIdKey).apply())
  }

  private[this] def trackCards(cards: Seq[Card], action: Action): TaskService[Unit] =
    TaskService {
      val tasks = cards map { card =>
        trackCard(card, action).value
      }
      Task.gatherUnordered(tasks) map (_ => Either.right(()))
    }

  private[this] def trackCard(card: Card, action: Action): TaskService[Unit] =
    card.cardType match {
      case AppCardType =>
        for {
          collection <- actions.getCurrentCollection.resolveOption(
            "Can't find the current collection in the UI")
          maybeCategory = collection.appsCategory map (c => Option(AppCategory(c))) getOrElse {
            collection.moment map (moment => MomentCategory(moment.momentType))
          }
          _ <- (action, card.packageName, maybeCategory) match {
            case (AddedToCollectionAction, Some(packageName), Some(category)) =>
              di.trackEventProcess.addAppToCollection(packageName, category)
            case (RemovedFromCollectionAction, Some(packageName), Some(category)) =>
              di.trackEventProcess.removeFromCollection(packageName, category)
            case _ => TaskService.empty
          }
        } yield ()
      case _ => TaskService.empty
    }

}
