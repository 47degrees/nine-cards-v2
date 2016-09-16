package com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections._
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models._
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceConfigurationException, ApiServices}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection

class SharedCollectionsProcessImpl(apiServices: ApiServices, persistenceServices: PersistenceServices)
  extends SharedCollectionsProcess
  with Conversions {

  val apiUtils = new ApiUtils(persistenceServices)

  override def getSharedCollection(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getSharedCollection(sharedCollectionId)(userConfig)
      maybeCollection <- persistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId)
    } yield toSharedCollection(response.sharedCollection, maybeCollection)).resolveLeft(mapLeft)

  override def getSharedCollectionsByCategory(
    category: NineCardCategory,
    typeShareCollection: TypeSharedCollection,
    offset: Int = 0,
    limit: Int = 50)
    (implicit context: ContextSupport): TaskService[Seq[SharedCollection]] =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getSharedCollectionsByCategory(category.name, typeShareCollection.name, offset, limit)(userConfig)
      localCollectionMap <- fetchSharedCollectionMap(response.items.map(_.sharedCollectionId))
    } yield toSharedCollections(response.items, localCollectionMap)).resolveLeft(mapLeft)

  override def getPublishedCollections()
    (implicit context: ContextSupport) = {
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getPublishedCollections()(userConfig)
      localCollectionMap <- fetchSharedCollectionMap(response.items.map(_.sharedCollectionId))
    } yield toSharedCollections(response.items, localCollectionMap)).resolveLeft(mapLeft)
  }

  private[this] def fetchSharedCollectionMap(sharedCollectionsIds: Seq[String]): TaskService[Map[String, Collection]] =
    for {
      localCollections <- persistenceServices.fetchCollectionsBySharedCollectionIds(sharedCollectionsIds)
    } yield localCollections.flatMap(c => c.sharedCollectionId.map(id => id -> c)).toMap

  override def createSharedCollection(
    sharedCollection: CreateSharedCollection)
    (implicit context: ContextSupport) = {
    import sharedCollection._
    (for {
      userConfig <- apiUtils.getRequestConfig
      result <- apiServices.createSharedCollection(name, description, author, packages, category.name, icon, community)(userConfig)
    } yield result.sharedCollectionId).resolveLeft(mapLeft)
  }

  override def updateSharedCollection(sharedCollection: UpdateSharedCollection)(implicit context: ContextSupport) = {
    import sharedCollection._
    (for {
      userConfig <- apiUtils.getRequestConfig
      result <- apiServices.updateSharedCollection(sharedCollectionId, Option(name), description, packages)(userConfig)
    } yield result.sharedCollectionId).resolveLeft(mapLeft)
  }

  override def getSubscriptions()(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      subscriptions <- apiServices.getSubscriptions()(userConfig)
      collections <- persistenceServices.fetchCollections
    } yield {

      val subscriptionsIds = subscriptions.items map (_.sharedCollectionId)

      val collectionsWithSharedCollectionId: Seq[(String, Collection)] =
        collections.flatMap(collection => collection.sharedCollectionId.map((_, collection)))

      (collectionsWithSharedCollectionId map {
        case (sharedCollectionId: String, collection: Collection) =>
          (sharedCollectionId, collection, subscriptionsIds.contains(sharedCollectionId))
      }) map toSubscription

    }).resolveLeft(mapLeft)

  override def subscribe(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      _ <- apiServices.subscribe(sharedCollectionId)(userConfig)
    } yield ()).resolveLeft(mapLeft)

  override def unsubscribe(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      _ <- apiServices.unsubscribe(sharedCollectionId)(userConfig)
    } yield ()).resolveLeft(mapLeft)
  
  private[this] def mapLeft[T]: (NineCardException) => Either[NineCardException, T] = {
    case e: ApiServiceConfigurationException => Left(SharedCollectionsConfigurationException(e.message, Some(e)))
    case e => Left(SharedCollectionsException(e.message, Some(e)))
  }

}
