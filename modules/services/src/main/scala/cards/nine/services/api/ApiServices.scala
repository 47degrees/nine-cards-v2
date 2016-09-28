package cards.nine.services.api

import cards.nine.commons.services.TaskService.TaskService
import cards.nine.services.api.models._

trait ApiServices {

  /**
   * Tries to login with the email and the device against backend V1
   * @param email user email
   * @param device user device
   * @return the [[cards.nine.services.api.LoginResponseV1]]
   * @throws ApiServiceV1ConfigurationException if the configuration is not valid or can't be found
   * @throws ApiServiceException if the user is not found or the request throws an Exception
   */
  def loginV1(
    email: String,
    device: LoginV1Device): TaskService[LoginResponseV1]

  /**
    * Fetches the user configuration associated to the user identified by the data in [[cards.nine.services.api.RequestConfigV1]]
    * @return the [[cards.nine.services.api.GetUserV1Response]] with the HTTP Code
    *         of the response and the [[cards.nine.services.api.models.UserV1]]
    * @throws ApiServiceV1ConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getUserConfigV1()(implicit requestConfig: RequestConfigV1): TaskService[GetUserV1Response]

  /**
    * Tries to login with the email, the androidId and the tokenId
    * @param email user email
    * @param androidId device identifier
    * @param tokenId token id obtained in the email authentication
    * @return the [[cards.nine.services.api.LoginResponse]]
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user is not found or the request throws an Exception
    */
  def login(
    email: String,
    androidId: String,
    tokenId: String): TaskService[LoginResponse]

  /**
   * Updates an existing user installation
   * @param deviceToken the token used for push notification
   * @param requestConfig necessary info for the headers
   * @return the [[cards.nine.services.api.UpdateInstallationResponse]] with the HTTP Code
   *         of the response
   * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
   * @throws ApiServiceException if there was an error in the request
   */
  def updateInstallation(deviceToken: Option[String])(implicit requestConfig: RequestConfig): TaskService[UpdateInstallationResponse]

  /**
   * Fetches the package info from Google Play given a package name
   * @param packageName the package identifier. For example `com.fortysevendeg.ninecardslauncher`
   * @param requestConfig necessary info for the headers
   * @return the [[cards.nine.services.api.GooglePlayPackageResponse]] with the HTTP Code
   *         of the response and a sequence of [[cards.nine.services.api.CategorizedPackage]]
   * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
   * @throws ApiServiceException if there was an error in the request
   */
  def googlePlayPackage(packageName: String)(implicit requestConfig: RequestConfig): TaskService[GooglePlayPackageResponse]

  /**
   * Fetches a list of packages information from Google Play given a list of package names. The response is similar to
   * {@link #googlePlayPackage(String)(RequestConfig) googlePlayPackage} but allow to fetch a list of packages with one operation.
   * @param packageNames a sequence of package identifiers
   * @param requestConfig necessary info for the headers
   * @return the [[cards.nine.services.api.GooglePlayPackagesResponse]] with the HTTP Code
   *         of the response and a sequence of [[cards.nine.services.api.CategorizedPackage]]
   * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
   * @throws ApiServiceException if there was an error in the request
   */
  def googlePlayPackages(packageNames: Seq[String])(implicit requestConfig: RequestConfig): TaskService[GooglePlayPackagesResponse]

  /**
   * Fetches a list of packages information from Google Play given a list of package names.
   * Differs from googlePlayPackages by providing more information
   * @param packageNames a sequence of package identifiers
   * @param requestConfig necessary info for the headers
   * @return the [[cards.nine.services.api.GooglePlayPackagesDetailResponse]] with the HTTP Code
   *         of the response and a sequence of [[cards.nine.services.api.CategorizedPackage]]
   * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
   * @throws ApiServiceException if there was an error in the request
   */
  def googlePlayPackagesDetail(packageNames: Seq[String])(implicit requestConfig: RequestConfig): TaskService[GooglePlayPackagesDetailResponse]

  /**
   * Fetches the recommended applications based on a category
   * @param category the category
   * @param excludePackages sequence of exclude packages
   * @param limit the maximum number of apps returned
   * @return the [[cards.nine.services.api.RecommendationResponse]] with the HTTP Code
   *         of the response and the sequence of recommended apps
   * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def getRecommendedApps(
    category: String,
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig): TaskService[RecommendationResponse]

  /**
   * Fetches the recommended applications based on other packages
   * @param packages the liked packages
   * @param excludePackages sequence of exclude packages
   * @param limit the maximum number of apps returned
   * @return the [[cards.nine.services.api.RecommendationResponse]] with the HTTP Code
   *         of the response and the sequence of recommended apps
   * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def getRecommendedAppsByPackages(
    packages: Seq[String],
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig): TaskService[RecommendationResponse]

  /**
    * Fetches the public collection
    * @param sharedCollectionId the public collection id
    * @return the TaskService containing a SharedCollectionResponse with the HTTP Code of the response and the
    *         collection or ApiServiceException if the user doesn't exists or there was an error in the request
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getSharedCollection(
    sharedCollectionId: String)(implicit requestConfig: RequestConfig): TaskService[SharedCollectionResponse]

  /**
    * Fetches the public collections based on some request params
    * @param category category of collections
    * @param collectionType type [top or latest]
    * @param offset offset of list
    * @param limit the maximum number of collection returned
    * @return the [[cards.nine.services.api.SharedCollectionResponseList]] with the HTTP Code
    *         of the response and the sequence of recommended collections
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getSharedCollectionsByCategory(
    category: String,
    collectionType: String,
    offset: Int,
    limit: Int)(implicit requestConfig: RequestConfig): TaskService[SharedCollectionResponseList]

  /**
    * Fetches the published collections
    * @return the [[cards.nine.services.api.SharedCollectionResponseList]] with the HTTP Code
    *         of the response and the sequence of recommended collections
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getPublishedCollections()(implicit requestConfig: RequestConfig): TaskService[SharedCollectionResponseList]

  /**
    * Persists a new shared collection
    * @param name The name of the collection
    * @param author The original author of the collection
    * @param packages The list of packages in the collection
    * @param icon The collection's icon
    * @param community A flag for whether this is a community collection
    * @return the [[cards.nine.services.api.CreateSharedCollectionResponse]] with the HTTP Code
    *         of the response and the sharedCollectionId
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the service is unable to create the shared collection
    */
  def createSharedCollection(
    name: String,
    author: String,
    packages: Seq[String],
    category: String,
    icon: String,
    community: Boolean)(implicit requestConfig: RequestConfig): TaskService[CreateSharedCollectionResponse]

  /**
    * Updates an existing  shared collection
    * @param sharedCollectionId The collection identifier
    * @param name The name of the collection
    * @param packages The list of packages in the collection
    * @return the [[cards.nine.services.api.UpdateSharedCollectionResponse]] with the HTTP Code
    *         of the response and the sharedCollectionId
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the service is unable to create the shared collection
    */
  def updateSharedCollection(
    sharedCollectionId: String,
    name: Option[String],
    packages: Seq[String])(implicit requestConfig: RequestConfig): TaskService[UpdateSharedCollectionResponse]

  /**
    * Fetches the subscriptions
    * @return the [[cards.nine.services.api.SubscriptionResponseList]] with the HTTP Code
    *         of the response and the sequence subscriptions
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getSubscriptions()(implicit requestConfig: RequestConfig): TaskService[SubscriptionResponseList]

  /**
    * Subscribes to a public collection
    * @param originalSharedCollectionId the public id of the collection to subscribe on
    * @return the [[cards.nine.services.api.SubscribeResponse]] with the HTTP Code
    *         of the response
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def subscribe(
    originalSharedCollectionId: String)(implicit requestConfig: RequestConfig): TaskService[SubscribeResponse]

  /**
    * Unsubscribes from a public collection
    * @param originalSharedCollectionId the public id of the collection to unsubscribe from
    * @return the [[cards.nine.services.api.UnsubscribeResponse]] with the HTTP Code
    *         of the response
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def unsubscribe(
    originalSharedCollectionId: String)(implicit requestConfig: RequestConfig): TaskService[UnsubscribeResponse]

  /**
    * Rank the packages by importance inside their category
    * @param packagesByCategorySeq a Sequence with the packages of the apps to rank ordered by its category
    * @param location the current country location of the device if it can be obtained
    * @return the [[cards.nine.services.api.RankAppsResponse]] with the HTTP Code
    *         of the response
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def rankApps(
    packagesByCategorySeq: Seq[PackagesByCategory],
    location: Option[String])(implicit requestConfig: RequestConfig): TaskService[RankAppsResponseList]

}
