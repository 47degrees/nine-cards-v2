package cards.nine.app.services.collections

import android.content.Intent
import cards.nine.app.commons.{BroadAction, Conversions, NineCardIntentConversions}
import cards.nine.app.services.commons.FirebaseExtensions._
import cards.nine.app.ui.commons.WizardState._
import cards.nine.app.ui.commons.action_filters.WizardStateActionFilter
import cards.nine.app.ui.commons._
import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.process.device.GetByName
import cards.nine.process.user.models.User
import com.google.android.gms.common.api.GoogleApiClient
import macroid.ContextWrapper
import monix.eval.Task

class CreateCollectionsJobs(actions: CreateCollectionsUiActions)(implicit contextWrapper: ContextWrapper)
  extends Jobs
  with Conversions
  with NineCardIntentConversions
  with ImplicitsJobExceptions {

  import CreateCollectionsService._

  var statuses = CreateCollectionsJobsStatuses()

  def sendActualState: TaskService[Unit] = {
    sendBroadCastTask(BroadAction(WizardStateActionFilter.action, statuses.currentState))
  }

  def startCommand(intent: Intent): TaskService[Unit] = {

    def readCloudId: TaskService[Unit] = TaskService {
      CatchAll[JobException] {
        val cloudId = Option(intent) flatMap (i => Option(i.getStringExtra(cloudIdKey)))
        statuses = statuses.copy(selectedCloudId = cloudId)
      }
    }

    def createAndConnectClient(email: String): TaskService[Unit] =
      for {
        apiClient <- di.cloudStorageProcess.createCloudStorageClient(email)
        _ <- TaskService(Task(Right(statuses = statuses.copy(apiClient = Some(apiClient)))))
        _ <- TaskService(CatchAll[JobException](apiClient.connect()))
      } yield ()

    def tryToStartService(user: User): TaskService[Unit] = {
      val hasKey = Option(intent) exists (_.hasExtra(cloudIdKey))
      (hasKey, user.deviceCloudId.isEmpty, user.email) match {
        case (true, true, Some(email)) =>
          for {
            _ <- setState(stateCreatingCollections)
            _ <- readCloudId
            _ <- actions.initialize()
            _ <- createAndConnectClient(email)
          } yield ()
        case (false, _, _) => setState(stateCloudIdNotSend, close = true)
        case (_, false, _) => setState(stateUserCloudIdPresent, close = true)
        case (_, _, None) => setState(stateUserEmailNotPresent, close = true)
      }
    }

    for {
      user <- di.userProcess.getUser
      _ <- tryToStartService(user)
    } yield ()

  }

  def createConfiguration(): TaskService[Unit] = {

    def loadConfiguration(
      client: GoogleApiClient,
      deviceToken: Option[String],
      cloudId: String): TaskService[Unit] = {
      for {
        _ <- di.deviceProcess.resetSavedItems()
        _ <- actions.setProcess(statuses.selectedCloudId, GettingAppsProcess)
        _ <- di.deviceProcess.saveInstalledApps
        apps <- di.deviceProcess.getSavedApps(GetByName)
        _ <- actions.setProcess(statuses.selectedCloudId, LoadingConfigProcess)
        device <- di.cloudStorageProcess.getCloudStorageDevice(client, cloudId)
        _ <- actions.setProcess(statuses.selectedCloudId, CreatingCollectionsProcess)
        _ <- di.collectionProcess.createCollectionsFromFormedCollections(toSeqFormedCollection(device.data.collections))
        momentSeq = device.data.moments map (_ map toSaveMomentRequest) getOrElse Seq.empty
        dockAppSeq = device.data.dockApps map (_ map toSaveDockAppRequest) getOrElse Seq.empty
        _ <- di.momentProcess.saveMoments(momentSeq)
        _ <- di.deviceProcess.saveDockApps(dockAppSeq)
        _ <- di.userProcess.updateUserDevice(device.data.deviceName, device.cloudId, deviceToken)
      } yield ()
    }

    (statuses.apiClient, statuses.selectedCloudId)  match {
      case (Some(client), Some(cloudId)) =>
        for {
          _ <- loadConfiguration(client, readToken, cloudId)
          _ <- setState(stateSuccess, close = true)
        } yield ()
      case (Some(client), None) =>
        TaskService.left(JobException("Device cloud id not received"))
      case _ =>
        TaskService.left(JobException("GoogleAPIClient not initialized"))
    }
  }

  def closeServiceWithError(): TaskService[Unit] = setState(stateFailure, close = true)

  private[this] def setState(state: String, close: Boolean = false): TaskService[Unit] = {
    statuses = statuses.copy(currentState = Option(state))
    for {
      _ <- sendActualState
      _ <- if (close) actions.endProcess else TaskService.right((): Unit)
    } yield ()
  }

}

case class CreateCollectionsJobsStatuses(
  selectedCloudId: Option[String] = None,
  currentState: Option[String] = None,
  apiClient: Option[GoogleApiClient] = None)
