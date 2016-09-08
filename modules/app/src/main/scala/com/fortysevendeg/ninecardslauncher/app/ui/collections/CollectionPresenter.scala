package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v7.widget.RecyclerView.ViewHolder
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppCardType
import com.fortysevendeg.ninecardslauncher.process.trackevent._
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

case class CollectionPresenter(
  animateCards: Boolean,
  maybeCollection: Option[Collection],
  actions: CollectionUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs { self =>

  def initialize(sType: ScrollType): Unit = {
    val canScroll = maybeCollection exists (_.cards.length > numSpaces)
    (actions.updateStatus(canScroll, sType) ~
      (maybeCollection map { collection =>
        actions.initialize(animateCards, collection)
      } getOrElse actions.showEmptyCollection())).run
  }

  def startReorderCards(holder: ViewHolder): Unit = if (!actions.isPulling()) actions.startReorder(holder).run

  def reorderCard(collectionId: Int, cardId: Int, position: Int): Unit = {
    Task.fork(di.collectionProcess.reorderCard(collectionId, cardId, position).value).resolveAsyncUi(
      onResult = (_) => actions.reloadCards()
    )
  }

  def moveToCollection(card: Card): Unit =
    Task.fork(di.collectionProcess.getCollections.value).resolveAsyncUi(
      onResult = (collections) => actions.moveToCollection(collections, card),
      onException = (_) => actions.showContactUsError())

  def editCard(collectionId: Int, cardId: Int, cardName: String): Unit = actions.editCard(collectionId, cardId, cardName)

  def addCards(cards: Seq[Card]): Unit = {
    cards foreach (card => trackCard(card, AddedToCollectionAction))
    actions.addCards(cards).run
  }

  def removeCard(card: Card): Unit = {
    trackCard(card, RemovedInCollectionAction)
    actions.removeCard(card).run
  }

  def reloadCards(cards: Seq[Card]): Unit = actions.reloadCards(cards).run

  def bindAnimatedAdapter(): Unit = maybeCollection foreach { collection =>
    actions.bindAnimatedAdapter(animateCards, collection).run
  }

  def saveEditedCard(collectionId: Int, cardId: Int, cardName: Option[String]): Unit = {

    def saveCard(collectionId: Int, cardId: Int, name: String) =
      for {
        card <- di.collectionProcess.editCard(collectionId, cardId, name)
      } yield card

    cardName match {
      case Some(name) if name.length > 0 =>
          Task.fork(saveCard(collectionId, cardId, name).value).resolveAsyncUi(
            onResult = (card) => actions.reloadCard(card),
            onException = (_) => actions.showContactUsError())
      case _ => actions.showMessageFormFieldError.run
    }
  }

  def showData(): Unit = maybeCollection foreach (c => actions.showData(c.cards.isEmpty).run)

  private[this] def trackCard(card: Card, action: Action): Unit = card.cardType match {
    case AppCardType =>
      for {
        collection <- actions.getCurrentCollection()
        packageName <- card.packageName
        category <- collection.appsCategory map (c => Option(AppCategory(c))) getOrElse {
          collection.moment flatMap (_.momentType) map MomentCategory
        }
      } yield {
        action match {
          case OpenCardAction =>
            Task.fork(di.trackEventProcess.openAppFromCollection(packageName, category).value).resolveAsync()
          case AddedToCollectionAction =>
            Task.fork(di.trackEventProcess.addAppToCollection(packageName, category).value).resolveAsync()
          case RemovedInCollectionAction =>
            Task.fork(di.trackEventProcess.removedInCollection(packageName, category).value).resolveAsync()
          case _ =>
        }
      }
    case _ =>
  }

}

trait CollectionUiActions {

  def initialize(animateCards: Boolean, collection: Collection): Ui[Any]

  def updateStatus(canScroll: Boolean, sType: ScrollType): Ui[Any]

  def startReorder(holder: ViewHolder): Ui[Any]

  def reloadCards(): Ui[Any]

  def bindAnimatedAdapter(animateCards: Boolean, collection: Collection): Ui[Any]

  def showMessageNotImplemented(): Ui[Any]

  def showContactUsError(): Ui[Any]

  def showMessageFormFieldError: Ui[Any]

  def showEmptyCollection(): Ui[Any]

  def moveToCollection(collections: Seq[Collection], card: Card): Ui[Any]

  def editCard(collectionId: Int, cardId: Int, cardName: String): Unit

  def addCards(cards: Seq[Card]): Ui[Any]

  def removeCard(card: Card): Ui[Any]

  def reloadCard(card: Card): Ui[Any]

  def reloadCards(cards: Seq[Card]): Ui[Any]

  def showData(emptyCollection: Boolean): Ui[Any]

  def isPulling(): Boolean

  def getCurrentCollection(): Option[Collection]

}