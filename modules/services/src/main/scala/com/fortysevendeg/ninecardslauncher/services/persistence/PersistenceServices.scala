package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.services.persistence.models._

trait PersistenceServices {

  /**
   * Obtains all the apps from the repository
   * @param orderBy indicates the field to order by
   * @param ascending indicates if it will be in ascending order or not
   * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.App]
   * @throws PersistenceServiceException if exist some problem obtaining the app
   */
  def fetchApps(orderBy: FetchAppOrder, ascending: Boolean = true): ServiceDef2[Seq[App], PersistenceServiceException]

  /**
    * Obtains iterable of apps from the repository
    * @param orderBy indicates the field to order by
    * @param ascending indicates if it will be in ascending order or not
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.IterableApps
    * @throws PersistenceServiceException if exist some problem obtaining the app
    */
  def fetchIterableApps(orderBy: FetchAppOrder, ascending: Boolean = true): ServiceDef2[IterableApps, PersistenceServiceException]

  /**
    * Obtains iterable of apps by keywords from the repository
    * @param keyword keyword for search
    * @param orderBy indicates the field to order by
    * @param ascending indicates if it will be in ascending order or not
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.IterableApps
    * @throws PersistenceServiceException if exist some problem obtaining the app
    */
  def fetchIterableAppsByKeyword(keyword: String, orderBy: FetchAppOrder, ascending: Boolean = true): ServiceDef2[IterableApps, PersistenceServiceException]

  /**
    * Obtains all the apps by category from the repository
    * @param category category for search
    * @param orderBy indicates the field to order by
    * @param ascending indicates if it will be in ascending order or not
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.App]
    * @throws PersistenceServiceException if exist some problem obtaining the app
    */
  def fetchAppsByCategory(category: String, orderBy: FetchAppOrder, ascending: Boolean = true): ServiceDef2[Seq[App], PersistenceServiceException]

  /**
    * Obtains iterable of apps by category from the repository
    * @param category category for search
    * @param orderBy indicates the field to order by
    * @param ascending indicates if it will be in ascending order or not
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.IterableApps
    * @throws PersistenceServiceException if exist some problem obtaining the apps
    */
  def fetchIterableAppsByCategory(category: String, orderBy: FetchAppOrder, ascending: Boolean = true): ServiceDef2[IterableApps, PersistenceServiceException]

  /**
    * Returns the number of times the first letter of a app is repeated alphabetically
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.DataCounter]
    * @throws PersistenceServiceException if exist some problem obtaining the apps
    */
  def fetchAlphabeticalAppsCounter: ServiceDef2[Seq[DataCounter], PersistenceServiceException]

  /**
    * Returns the number of times in every category alphabetically
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.DataCounter]
    * @throws PersistenceServiceException if exist some problem obtaining the apps
    */
  def fetchCategorizedAppsCounter: ServiceDef2[Seq[DataCounter], PersistenceServiceException]

  /**
    * Returns the number of times by installation date
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.DataCounter]
    * @throws PersistenceServiceException if exist some problem obtaining the apps
    */
  def fetchInstallationDateAppsCounter: ServiceDef2[Seq[DataCounter], PersistenceServiceException]

  /**
   * Obtains an app from the repository by the package name
   * @param packageName the package name of the app to get
   * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.App]
   * @throws PersistenceServiceException if exist some problem obtaining the app
   */
  def findAppByPackage(packageName: String): ServiceDef2[Option[App], PersistenceServiceException]

  /**
   * Adds an app to the repository
   * @param request includes the necessary data to create a new app in the repository
   * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.App
   * @throws PersistenceServiceException if exist some problem obtaining the app
   */
  def addApp(request: AddAppRequest): ServiceDef2[App, PersistenceServiceException]

  /**
    * Deletes all apps from the repository by the where clause
    * @return an Int if the apps has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the apps
    */
  def deleteAllApps(): ServiceDef2[Int, PersistenceServiceException]

  /**
   * Deletes an app from the repository by the package name
   * @param packageName the package name of the app to delete
   * @return an Int if the app has been deleted correctly
   * @throws PersistenceServiceException if exist some problem deleting the app
   */
  def deleteAppByPackage(packageName: String): ServiceDef2[Int, PersistenceServiceException]

  /**
   * Updates the data of an app from the repository
   * @param request includes the data to update the app
   * @return an Int if the app has been updated correctly
   * @throws PersistenceServiceException if exist some problem updating the app
   */
  def updateApp(request: UpdateAppRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Adds an card to the repository
    * @param request includes the necessary data to create a new card in the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.Card
    * @throws PersistenceServiceException if exist some problem creating the card
    */
  def addCard(request: AddCardRequest): ServiceDef2[Card, PersistenceServiceException]

  /**
    * Deletes all cards from the repository by the where clause
    * @return an Int if the cards has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the cards
    */
  def deleteAllCards(): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Deletes a card from the repository by the card
    * @param request includes the card to delete
    * @return an Int if the card has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the card
    */
  def deleteCard(request: DeleteCardRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Deletes the cards from the repository by the collection id
    * @param collectionId the id of the collection that contains the cards
    * @return an Int if the cards have been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the cards
    */
  def deleteCardsByCollection(collectionId: Int): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Obtains all the cards from the repository by the collection id
    * @param request includes the id of the collection
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Card]
    * @throws PersistenceServiceException if exist some problem obtaining the cards
    */
  def fetchCardsByCollection(request: FetchCardsByCollectionRequest): ServiceDef2[Seq[Card], PersistenceServiceException]

  /**
    * Obtains all the cards from the repository
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Card]
    * @throws PersistenceServiceException if exist some problem obtaining the cards
    */
  def fetchCards: ServiceDef2[Seq[Card], PersistenceServiceException]

  /**
    * Obtains a card from the repository by the id
    * @param request includes the id of the card to find
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Card]
    * @throws PersistenceServiceException if exist some problem obtaining the card
    */
  def findCardById(request: FindCardByIdRequest): ServiceDef2[Option[Card], PersistenceServiceException]

  /**
    * Updates the data of an card from the repository
    * @param request includes the data to update the card
    * @return an Int if the card has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the card
    */
  def updateCard(request: UpdateCardRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Bulk update of the data of some cards from the repository
    * @param request includes the data to update the cards
    * @return a Seq[Int] if the cards has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the card
    */
  def updateCards(request: UpdateCardsRequest): ServiceDef2[Seq[Int], PersistenceServiceException]

  /**
    * Adds an collection to the repository
    * @param request includes the necessary data to create a new collection in the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection
    * @throws PersistenceServiceException if exist some problem creating the collection
    */
  def addCollection(request: AddCollectionRequest): ServiceDef2[Collection, PersistenceServiceException]

  /**
    * Deletes all collections from the repository by the where clause
    * @return an Int if the collections has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the collections
    */
  def deleteAllCollections(): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Deletes a collection from the repository by the collection
    * @param request includes the collection to delete
    * @return an Int if the collection has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the collection
    */
  def deleteCollection(request: DeleteCollectionRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Obtains all the collections from the repository
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection]
    * @throws PersistenceServiceException if exist some problem obtaining the collections
    */
  def fetchCollections: ServiceDef2[Seq[Collection], PersistenceServiceException]

  /**
    * Obtains the collection from the repository by the sharedCollection id
    * @param request includes the id of the sharedCollection
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection]
    * @throws PersistenceServiceException if exist some problem obtaining the collection
    */
  def fetchCollectionBySharedCollection(request: FetchCollectionBySharedCollectionRequest): ServiceDef2[Option[Collection], PersistenceServiceException]

  /**
    * Obtains the collection from the repository by the position
    * @param request includes the position
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection]
    * @throws PersistenceServiceException if exist some problem obtaining the collection
    */
  def fetchCollectionByPosition(request: FetchCollectionByPositionRequest): ServiceDef2[Option[Collection], PersistenceServiceException]

  /**
    * Obtains a collection from the repository by the id
    * @param request includes the id of the collection to find
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection]
    * @throws PersistenceServiceException if exist some problem obtaining the collection
    */
  def findCollectionById(request: FindCollectionByIdRequest): ServiceDef2[Option[Collection], PersistenceServiceException]

  /**
    * Updates the data of an collection from the repository
    * @param request includes the data to update the collection
    * @return an Int if the collection has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the collection
    */
  def updateCollection(request: UpdateCollectionRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Obtains the android id from the repository
    * @return an String with the android id
    * @throws AndroidIdNotFoundException if exist some problem obtaining the android id
    */
  def getAndroidId(implicit context: ContextSupport): ServiceDef2[String, AndroidIdNotFoundException]

  /**
   * Adds an user to the repository
   * @param request includes the necessary data to create a new user in the repository
   * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.User
   * @throws PersistenceServiceException if exist some problem creating the user
   */
  def addUser(request: AddUserRequest): ServiceDef2[User, PersistenceServiceException]

  /**
    * Deletes all users from the repository by the where clause
    * @return an Int if the users has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the users
    */
  def deleteAllUsers(): ServiceDef2[Int, PersistenceServiceException]

  /**
   * Deletes an user from the repository by the user
   * @param request includes the user to delete
   * @return an Int if the user has been deleted correctly
   * @throws PersistenceServiceException if exist some problem deleting the user
   */
  def deleteUser(request: DeleteUserRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
   * Obtains all the users from the repository
   * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.User]
   * @throws PersistenceServiceException if exist some problem obtaining the users
   */
  def fetchUsers: ServiceDef2[Seq[User], PersistenceServiceException]

  /**
   * Obtains an user from the repository by the id
   * @param request includes the user id  of the user to get
   * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.User]
   * @throws PersistenceServiceException if exist some problem obtaining the user
   */
  def findUserById(request: FindUserByIdRequest): ServiceDef2[Option[User], PersistenceServiceException]

  /**
   * Updates the data of an user from the repository
   * @param request includes the data to update the user
   * @return an Int if the user has been updated correctly
   * @throws PersistenceServiceException if exist some problem updating the user
   */
  def updateUser(request: UpdateUserRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Creates or updates dock app to the repository
    * @param request includes the necessary data to create a new dock app in the repository
    * @throws PersistenceServiceException if exist some problem creating or updating the dock app
    */
  def createOrUpdateDockApp(request: CreateOrUpdateDockAppRequest): ServiceDef2[Unit, PersistenceServiceException]

  /**
    * Deletes all dock apps from the repository by the where clause
    * @return an Int if the dock apps has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the dock apps
    */
  def deleteAllDockApps(): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Deletes a dock app from the repository by the dock app
    * @param request includes the dock app to delete
    * @return an Int if the dock app has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the dock app
    */
  def deleteDockApp(request: DeleteDockAppRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Obtains all the dock apps from the repository
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.DockApp]
    * @throws PersistenceServiceException if exist some problem obtaining the dock apps
    */
  def fetchDockApps: ServiceDef2[Seq[DockApp], PersistenceServiceException]

  /**
    * Obtains iterable of dock apps from the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.IterableDockApps
    * @throws PersistenceServiceException if exist some problem obtaining the dock apps
    */
  def fetchIterableDockApps: ServiceDef2[IterableDockApps, PersistenceServiceException]

  /**
    * Obtains a dock app from the repository by the id
    * @param request includes the dock app id  of the dock app to get
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.DockApp]
    * @throws PersistenceServiceException if exist some problem obtaining the dock app
    */
  def findDockAppById(request: FindDockAppByIdRequest): ServiceDef2[Option[DockApp], PersistenceServiceException]

  /**
    * Adds an moment to the repository
    * @param request includes the necessary data to create a new moment in the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.Moment
    * @throws PersistenceServiceException if exist some problem creating the moment
    */
  def addMoment(request: AddMomentRequest): ServiceDef2[Moment, PersistenceServiceException]

  /**
    * Deletes all moments from the repository by the where clause
    * @return an Int if the moments has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the moments
    */
  def deleteAllMoments(): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Deletes an moment from the repository by the moment
    * @param request includes the moment to delete
    * @return an Int if the moment has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the moment
    */
  def deleteMoment(request: DeleteMomentRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Obtains all the moments from the repository
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Moment]
    * @throws PersistenceServiceException if exist some problem obtaining the moments
    */
  def fetchMoments: ServiceDef2[Seq[Moment], PersistenceServiceException]

  /**
    * Obtains an moment from the repository by the id
    * @param request includes the moment id  of the moment to get
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Moment]
    * @throws PersistenceServiceException if exist some problem obtaining the moment
    */
  def findMomentById(request: FindMomentByIdRequest): ServiceDef2[Option[Moment], PersistenceServiceException]

  /**
    * Updates the data of an moment from the repository
    * @param request includes the data to update the moment
    * @return an Int if the moment has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the moment
    */
  def updateMoment(request: UpdateMomentRequest): ServiceDef2[Int, PersistenceServiceException]

}
