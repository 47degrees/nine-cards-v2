package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.repository.model.{Card => RepositoryCard, CardData => RepositoryCardData, CardsWithCollectionId}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Card

trait CardConversions {

  def toCardsWithCollectionId(request: AddCardWithCollectionIdRequest): CardsWithCollectionId =
    CardsWithCollectionId(
      collectionId = request.collectionId,
      data = request.cards map toRepositoryCardData)

  def toCard(card: RepositoryCard): Card = {
    Card(
      id = card.id,
      position = card.data.position,
      term = card.data.term,
      packageName = card.data.packageName,
      cardType = card.data.cardType,
      intent = card.data.intent,
      imagePath = card.data.imagePath,
      notification = card.data.notification)
  }

  def toRepositoryCard(request: UpdateCardRequest): RepositoryCard =
    RepositoryCard(
      id = request.id,
      data = RepositoryCardData(
        position = request.position,
        term = request.term,
        packageName = request.packageName,
        cardType = request.cardType,
        intent = request.intent,
        imagePath = request.imagePath,
        notification = request.notification
      )
    )

  def toRepositoryCardData(request: AddCardRequest): RepositoryCardData =
    RepositoryCardData(
      position = request.position,
      term = request.term,
      packageName = request.packageName,
      cardType = request.cardType,
      intent = request.intent,
      imagePath = request.imagePath,
      notification = request.notification)
}
