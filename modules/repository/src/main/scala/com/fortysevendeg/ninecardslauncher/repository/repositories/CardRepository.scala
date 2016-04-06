package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCard
import com.fortysevendeg.ninecardslauncher.repository.model.{Card, CardData}
import com.fortysevendeg.ninecardslauncher.repository.provider.{CardEntity, NineCardsUri}
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import IterableCursor._
import RepositoryUtils._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._

import scala.language.postfixOps
import scalaz.concurrent.Task

class CardRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val cardUri = uriCreator.parse(cardUriString)

  val cardNotificationUri = uriCreator.parse(cardUriNotificationString)

  def addCard(collectionId: Int, data: CardData): ServiceDef2[Card, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            position -> data.position,
            CardEntity.collectionId -> collectionId,
            term -> data.term,
            packageName -> flatOrNull(data.packageName),
            cardType -> data.cardType,
            intent -> data.intent,
            imagePath -> data.imagePath,
            starRating -> (data.starRating orNull),
            micros -> data.micros,
            numDownloads -> flatOrNull(data.numDownloads),
            notification -> flatOrNull(data.notification))

          val id = contentResolverWrapper.insert(
            uri = cardUri,
            values = values,
            notificationUri = Some(cardNotificationUri))

          Card(id = id, data = data)
        }
      }
    }

  def deleteCards(where: String = ""): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = cardUri,
            where = where,
            notificationUri = Some(cardNotificationUri))
        }
      }
    }

  def deleteCard(card: Card): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = cardUri,
            id = card.id,
            notificationUri = Some(cardNotificationUri))
        }
      }
    }

  def findCardById(id: Int): ServiceDef2[Option[Card], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = cardUri,
            id = id,
            projection = allFields)(getEntityFromCursor(cardEntityFromCursor)) map toCard
        }
      }
    }

  def fetchCardsByCollection(collectionId: Int): ServiceDef2[Seq[Card], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = cardUri,
            projection = allFields,
            where = s"${CardEntity.collectionId} = ?",
            whereParams = Seq(collectionId.toString),
            orderBy = s"${CardEntity.position} asc")(getListFromCursor(cardEntityFromCursor)) map toCard
        }
      }
    }

  def fetchCards: ServiceDef2[Seq[Card], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = cardUri,
            projection = allFields)(getListFromCursor(cardEntityFromCursor)) map toCard
        }
      }
    }

  def fetchIterableCards(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): ServiceDef2[IterableCursor[Card], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = cardUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(cardFromCursor)
        }
      }
    }

  def updateCard(card: Card): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = createMapValues(card)

          contentResolverWrapper.updateById(
            uri = cardUri,
            id = card.id,
            values = values,
            notificationUri = Some(cardNotificationUri))
        }
      }
    }

  def updateCards(cards: Seq[Card]): ServiceDef2[Seq[Int], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = cards map { card =>
            (card.id, createMapValues(card))
          }

          contentResolverWrapper.updateByIds(
            authority = NineCardsUri.authorityPart,
            uri = cardUri,
            idAndValues = values,
            notificationUri = Some(cardNotificationUri))
        }
      }
    }

  private[this] def createMapValues(card: Card) =
    Map[String, Any](
      position -> card.data.position,
      term -> card.data.term,
      packageName -> (card.data.packageName orNull),
      cardType -> card.data.cardType,
      intent -> card.data.intent,
      imagePath -> card.data.imagePath,
      starRating -> (card.data.starRating orNull),
      micros -> card.data.micros,
      numDownloads -> (card.data.numDownloads orNull),
      notification -> (card.data.notification orNull))
}
