package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Build
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.permissions.PermissionChecker
import com.fortysevendeg.ninecardslauncher.app.permissions.PermissionChecker.ReadContacts
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.RequestCodes._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SafeUi._
import com.fortysevendeg.ninecardslauncher.app.ui.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.UserCloudDevices
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.google._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.accounts.AccountsProcessOperationCancelledException
import com.fortysevendeg.ninecardslauncher.process.cloud.Conversions
import com.fortysevendeg.ninecardslauncher.process.cloud.models.{CloudStorageDeviceData, CloudStorageDeviceSummary}
import com.fortysevendeg.ninecardslauncher.process.userv1.UserV1ConfigurationException
import com.fortysevendeg.ninecardslauncher.process.userv1.models.UserV1Device
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ActivityContextWrapper
import monix.eval.Task

import scala.util.{Failure, Success, Try}

/**
  * This class manages all jobs over the Wizard. This is the ideal flow:
  *  - Activity calls to 'Job.initialize'
  *   + Job calls to 'UiAction.initialize'
  *  - UiAction calls to 'Job.connectAccount'
  *   + Job starts a new activity for getting the account
  *  - Activity calls to 'Job.activityResult'
  *   + Job calls to 'Job.requestAndroidMarketPermission' that fetches the Android Market token
  *   + Job calls to 'Job.requestGooglePermission' that fetches the Google Profile token
  *   + Job calls to 'Job.tryToConnectDriveApiClient' that connects the Drive client
  *  - GoogleDriveApiClientProvider calls 'Job.onDriveConnected'
  *   + Job execute 'Job.googleSignIn'
  *   + Job starts a new activity for Google Profile sign in
  *  - Activity calls to 'Job.activityResult'
  *   + Job fetches the tokenId and calls to 'Job.tryToConnectGoogleApiClient'
  *  - GooglePlusApiClientProvider calls to 'Job.onPlusConnected'
  *   + Job update the user profile information and calls to 'Job.loadDevices'
  *   + Job calls to 'UiAction.showDevices' with the loaded devices
  *  - UiAction calls to 'Job.deviceSelected'
  *   + Job asks for Contacts permissions and calls to 'Job.generateCollections'
  *   + Job starts the service
  *  - Activity calls to 'Job.serviceFinished'
  *   + Job calls to 'UiAction.showDiveIn'
  *  - UiAction calls to 'Job.finishWizard'
  *   + Job set the result RESULT_OK and finish the activity
  */
class WizardJobs(actions: WizardUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with ImplicitsUiExceptions {

  val accountType = "com.google"

  val tagDialog = "wizard-dialog"

  val permissionChecker = new PermissionChecker

  var clientStatuses = WizardJobsStatuses()

  def initialize(): TaskService[Unit] = actions.initialize()

  def stop(): TaskService[Unit] = {

    def tryToClose(maybeClient: Option[GoogleServiceClient]): Try[Unit] =
      maybeClient map (c => Try(c.disconnect())) getOrElse Success((): Unit)

    TaskService {
      Task {
        List(clientStatuses.driveApiClient, clientStatuses.plusApiClient) map tryToClose collect {
          case Failure(e) => e
        } match {
          case Nil => Right((): Unit)
          case list =>
            val message = s"Error disconnecting clients:\n ${list.map(_.getMessage).mkString(" \n")}"
            Left(UiException(message, cause = None))
        }
      }
    }
  }

  def connectAccount(termsAccepted: Boolean): TaskService[Unit] =
    if (termsAccepted) {
      val intent = AccountManager
        .newChooseAccountIntent(javaNull, javaNull, Array(accountType), javaNull, javaNull, javaNull, javaNull)
      uiStartIntentForResult(intent, RequestCodes.selectAccount).toService
    } else {
      actions.showErrorAcceptTerms()
    }

  def deviceSelected(maybeKey: Option[String]): TaskService[Unit] = {
    clientStatuses = clientStatuses.copy(deviceKey = maybeKey)
    if (permissionChecker.havePermission(ReadContacts)) {
      generateCollections(maybeKey)
    } else {
      requestContactsPermission()
    }
  }

  def requestContactsPermission(): TaskService[Unit] =
    TaskService {
      CatchAll[UiException] {
        permissionChecker.requestPermission(RequestCodes.contactsPermission, ReadContacts)
      }
    }

  def contactsPermissionDenied(): TaskService[Unit] =
    generateCollections(clientStatuses.deviceKey)

  def finishWizard(): TaskService[Unit] = TaskService {
    CatchAll[UiException] {
      activityContextSupport.getActivity match {
        case Some(activity) =>
          activity.setResult(Activity.RESULT_OK)
          activity.finish()
        case None => throw new NullPointerException("Activity instance not found")
      }
    }
  }

  def serviceCreatingCollections(): TaskService[Unit] = actions.goToWizard()

  def serviceUnknownError(): TaskService[Unit] = actions.goToUser()

  def serviceCloudIdNotSentError(): TaskService[Unit] = actions.goToUser()

  def serviceCloudIdAlreadySetError(): TaskService[Unit] =
    for {
      _ <- di.userProcess.unregister
      _ <- actions.goToUser()
    } yield ()

  def serviceFinished(): TaskService[Unit] = actions.showDiveIn()

  def activityResult(requestCode: Int, resultCode: Int, data: Intent): TaskService[Unit] =
    (requestCode, resultCode) match {
      case (RequestCodes.selectAccount, Activity.RESULT_OK) =>
        clientStatuses = clientStatuses.copy(email = Option(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)))
        requestAndroidMarketPermission()
      case (RequestCodes.selectAccount, _) =>
        actions.showSelectAccountDialog()
      case (`resolveGooglePlayConnection`, Activity.RESULT_OK) =>
        tryToConnectDriveApiClient()
      case (`resolveGooglePlayConnection`, _) =>
        actions.showErrorConnectingGoogle()
      case (`resolveConnectedUser`, Activity.RESULT_OK) =>
        val mailTokenId = clientStatuses.plusApiClient flatMap (_.readTokenIdFromSignIn(data))
        clientStatuses = clientStatuses.copy(mailTokenId = mailTokenId)
        tryToConnectGoogleApiClient()
      case (`resolveConnectedUser`, _) =>
        actions.showErrorConnectingGoogle()
      case _ => TaskService(Task(Right((): Unit)))
    }

  def requestAndroidMarketPermission(): TaskService[Unit] = {

    def invalidateToken(): TaskService[Unit] =
      clientStatuses.androidMarketToken match {
        case Some(token) => di.userAccountsProcess.invalidateToken(token)
        case None => TaskService(Task(Right((): Unit)))
      }

    def storeDriveApiClient(driveApiClient: GoogleServiceClient): Unit =
      clientStatuses = clientStatuses.copy(driveApiClient = Option(driveApiClient))

    def storeAndroidMarketToken(token: String): Unit =
      clientStatuses = clientStatuses.copy(androidMarketToken = Option(token))

    clientStatuses.email match {
      case Some(account) =>
        for {
          googleApiClient <- di.cloudStorageProcess.createCloudStorageClient(account)
          _ = storeDriveApiClient(googleApiClient)
          _ <- actions.showLoading()
          _ <- invalidateToken()
          token <- di.userAccountsProcess
            .getAuthToken(account, resGetString(R.string.android_market_oauth_scopes))
            .resolveLeft {
              case ex: AccountsProcessOperationCancelledException =>
                Left(WizardMarketTokenRequestCancelledException(ex.getMessage, Some(ex)))
              case ex: Throwable => Left(ex)
            }
          _ = storeAndroidMarketToken(token)
          _ <- requestGooglePermission()
        } yield ()
      case None => actions.goToUser()
    }

  }

  def requestGooglePermission(): TaskService[Unit] =
    clientStatuses.email match {
      case Some(account) =>
        for {
          _ <- actions.showLoading()
          _ <- di.userAccountsProcess
            .getAuthToken(account, resGetString(R.string.profile_and_drive_oauth_scopes))
            .resolveLeft {
              case ex: AccountsProcessOperationCancelledException =>
                Left(WizardGoogleTokenRequestCancelledException(ex.getMessage, Some(ex)))
              case ex: Throwable => Left(ex)
            }
          _ <- tryToConnectDriveApiClient()
        } yield ()
      case None => actions.goToUser()
    }

  def requestPermissionsResult(requestCode: Int, permissions: Array[String], grantResults: Array[Int]): TaskService[Unit] =
    if (requestCode == RequestCodes.contactsPermission) {
      val result = permissionChecker.readPermissionRequestResult(permissions, grantResults)
      if (result.exists(_.hasPermission(ReadContacts))) {
        generateCollections(clientStatuses.deviceKey)
      } else if (permissionChecker.shouldRequestPermission(ReadContacts)) {
        actions.showRequestContactsPermissionDialog()
      } else {
        generateCollections(clientStatuses.deviceKey)
      }
    } else {
      TaskService(Task(Right((): Unit)))
    }

  def errorOperationMarketTokenCancelled(): TaskService[Unit] = actions.showMarketPermissionDialog()

  def errorOperationGoogleTokenCancelled(): TaskService[Unit] = actions.showGooglePermissionDialog()

  def driveConnected(): TaskService[Unit] = googleSignIn()

  def driveConnectionFailed(connectionResult: GoogleServiceClientError): TaskService[Unit] =
    onConnectionFailed(connectionResult)

  def plusConnected(): TaskService[Unit] =
    clientStatuses.plusApiClient match {
      case Some(apiClient) =>
        for {
          _ <- actions.showLoading()
          maybeProfileName <- di.socialProfileProcess.updateUserProfile(apiClient)
          _ <- loadDevices(maybeProfileName)
        } yield ()
      case None => actions.goToUser()
    }

  def plusConnectionFailed(connectionResult: GoogleServiceClientError): TaskService[Unit] =
    onConnectionFailed(connectionResult)

  def googleSignIn(): TaskService[Unit] = {

    def storePlusApiClient(plusApiClient: GoogleServiceClient): Unit =
      clientStatuses = clientStatuses.copy(plusApiClient = Option(plusApiClient))

    def signInIntentService(plusApiClient: GoogleServiceClient): TaskService[Unit] =
      uiStartIntentForResult(plusApiClient.getSignInIntent, resolveConnectedUser).toService

    clientStatuses.email match {
      case Some(email) =>
        for {
          plusApiClient <- di.socialProfileProcess.createSocialProfileClient(
            clientId = contextSupport.getResources.getString(R.string.api_v2_client_id),
            account = email)
          _ = storePlusApiClient(plusApiClient)
          _ <- signInIntentService(plusApiClient)
        } yield ()
      case None => actions.goToUser()
    }
  }

  private[this] def onConnectionFailed(connectionResult: GoogleServiceClientError): TaskService[Unit] = {

    def showErrorDialog(): TaskService[Unit] = withActivity { activity =>
      TaskService {
        CatchAll[UiException] {
          connectionResult.getErrorDialog(activity, resolveGooglePlayConnection).show()
        }
      }
    }

    if (connectionResult.hasResolution) {
      withActivity { activity =>
        TaskService {
          CatchAll[UiException] {
            val pendingIntent = connectionResult.getResolution
            activity.startIntentSenderForResult(pendingIntent.getIntentSender, resolveGooglePlayConnection, javaNull, 0, 0, 0)
          }
        }
      }
    } else if (connectionResult.shouldShowErrorDialog) {
      for {
        _ <- actions.goToUser()
        _ <- showErrorDialog()
      } yield ()
    } else {
      actions.showErrorConnectingGoogle()
    }
  }

  private[this] def generateCollections(maybeKey: Option[String]): TaskService[Unit] = {
    val intent = activityContextSupport.createIntent(classOf[CreateCollectionService])
    intent.putExtra(CreateCollectionService.cloudIdKey, maybeKey.getOrElse(CreateCollectionService.newConfiguration))
    for {
      _ <- uiStartServiceIntent(intent).toService
      _ <- actions.goToWizard()
    } yield ()
  }

  private[this] def tryToConnectDriveApiClient(): TaskService[Unit] =
    clientStatuses.driveApiClient match {
      case Some(client) => TaskService(CatchAll[UiException](client.connect()))
      case None => requestAndroidMarketPermission()
    }

  private[this] def tryToConnectGoogleApiClient(): TaskService[Unit] =
    clientStatuses.plusApiClient match {
      case Some(client) => TaskService(CatchAll[UiException](client.connectSignInModeOptional()))
      case None => googleSignIn()
    }

  private[this] def loadDevices(maybeProfileName: Option[String]): TaskService[Unit] = {

    def storeOnCloud(client: GoogleServiceClient, cloudStorageDevices: Seq[CloudStorageDeviceData]) =
      TaskService {
        val tasks = cloudStorageDevices map { deviceData =>
          di.cloudStorageProcess.createOrUpdateCloudStorageDevice(
            client = client,
            maybeCloudId = None,
            cloudStorageDevice = deviceData).value
        }
        Task.gatherUnordered(tasks) map { list =>
          Right(list.collect {
            case Right(r) => r
          })
        }
      }

    // If we found some error when connecting to Backend V1 we just return an empty collection of devices
    def loadDevicesFromV1(): TaskService[Seq[UserV1Device]] =
      di.userV1Process.getUserInfo(Build.MODEL, Seq(resGetString(R.string.android_market_oauth_scopes)))
        .map(_.devices)
        .resolveRight {
          case e: UserV1ConfigurationException =>
            AppLog.info("Invalid configuration for backend V1")
            Right(Seq.empty)
          case e => Right(Seq.empty)
        }

    def verifyAndUpdate(
      client: GoogleServiceClient,
      email: String,
      cloudStorageResources: Seq[CloudStorageDeviceSummary]) = {

      import Conversions._

      if (cloudStorageResources.isEmpty) {
        for {
          userInfoDevices <- loadDevicesFromV1()
          cloudStorageDevices <- storeOnCloud(client, userInfoDevices map toCloudStorageDevice)
          actualDevice <- di.cloudStorageProcess.prepareForActualDevice(client, cloudStorageDevices)
          (maybeUserDevice, devices) = actualDevice
        } yield {
          UserCloudDevices(

            name = maybeProfileName getOrElse email,
            userDevice = maybeUserDevice map toUserCloudDevice,
            devices = devices map toUserCloudDevice)
        }
      } else {
        for {
          actualDevice <- di.cloudStorageProcess.prepareForActualDevice(client, cloudStorageResources)
          (maybeUserDevice, devices) = actualDevice
        } yield {
          UserCloudDevices(
            name = maybeProfileName getOrElse email,
            userDevice = maybeUserDevice map toUserCloudDevice,
            devices = devices map toUserCloudDevice)
        }
      }
    }

    def loadCloudDevices(
      client: GoogleServiceClient,
      email: String,
      androidMarketToken: String,
      emailTokenId: String) = {
      for {
        _ <- di.userProcess.signIn(email, androidMarketToken, emailTokenId)
        cloudStorageResources <- di.cloudStorageProcess.getCloudStorageDevices(client)
        userCloudDevices <- verifyAndUpdate(client, email, cloudStorageResources).resolveLeftTo(UserCloudDevices(email, None, Seq.empty))
      } yield userCloudDevices

    }

    clientStatuses match {
      case WizardJobsStatuses(_, Some(client), _, Some(email), Some(androidMarketToken), Some(emailTokenId)) =>
        for {
          _ <- actions.showLoading()
          devices <- loadCloudDevices(client, email, androidMarketToken, emailTokenId)
          _ <- actions.showDevices(devices)
        } yield ()
      case _ => actions.showErrorConnectingGoogle()
    }

  }

}

case class WizardJobsStatuses(
  deviceKey: Option[String] = None,
  driveApiClient: Option[GoogleServiceClient] = None,
  plusApiClient: Option[GoogleServiceClient] = None,
  email: Option[String] = None,
  androidMarketToken: Option[String] = None,
  mailTokenId: Option[String] = None)