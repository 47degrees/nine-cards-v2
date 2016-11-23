package cards.nine.app.ui.launcher.jobs

import cards.nine.app.di.Injector
import cards.nine.app.observers.ObserverRegister
import cards.nine.app.receivers.moments.MomentBroadcastReceiver
import cards.nine.app.ui.commons.{BroadAction, MomentPreferences, RequestCodes}
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, LauncherData, LauncherMoment, MomentWorkSpace}
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.exceptions.{ChangeMomentException, LoadDataException}
import cards.nine.app.ui.launcher.jobs.uiactions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.{CollectionTestData, DockAppTestData, UserTestData}
import cards.nine.commons.utils.FileUtils
import cards.nine.models.types._
import cards.nine.process.accounts.UserAccountsProcess
import cards.nine.process.collection.CollectionProcess
import cards.nine.process.device.DeviceProcess
import cards.nine.process.intents.LauncherExecutorProcess
import cards.nine.process.moment.{MomentException, MomentProcess}
import cards.nine.process.recognition.RecognitionProcess
import cards.nine.process.theme.ThemeProcess
import cards.nine.process.thirdparty.ExternalServicesProcess
import cards.nine.process.trackevent.TrackEventProcess
import cards.nine.process.user.{UserException, UserProcess}
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait LauncherJobsSpecification extends TaskServiceSpecification
  with Mockito {

  trait LauncherJobsScope
    extends Scope
      with LauncherTestData
      with DockAppTestData
      with CollectionTestData
      with UserTestData {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    lazy implicit val contextSupport = mock[ContextSupport]

    val mockInjector = mock[Injector]

    val mockLauncherDOM = mock[LauncherDOM]

    val mockLauncherUiActions = mock[LauncherUiActions]

    mockLauncherUiActions.dom returns mockLauncherDOM

    val mockMenuDrawersUiActions = mock[MenuDrawersUiActions]

    val mockAppDrawerUiActions = mock[AppDrawerUiActions]

    mockAppDrawerUiActions.dom returns mockLauncherDOM

    val mockNavigationUiActions = mock[NavigationUiActions]

    val mockDockAppsUiActions = mock[DockAppsUiActions]

    val mockTopBarUiActionss = mock[TopBarUiActions]

    val mockWorkspaceUiActions = mock[WorkspaceUiActions]

    val mockWidgetUiActions = mock[WidgetUiActions]

    val mockDragUiActions = mock[DragUiActions]

    mockDragUiActions.dom returns mockLauncherDOM

    val mockDeviceProcess = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcess

    val mockThemeProcess = mock[ThemeProcess]

    mockInjector.themeProcess returns mockThemeProcess

    val mockFileUtils = mock[FileUtils]

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockLauncherExecutorProcess = mock[LauncherExecutorProcess]

    mockInjector.launcherExecutorProcess returns mockLauncherExecutorProcess

    val mockExternalServicesProcesss = mock[ExternalServicesProcess]

    mockInjector.externalServicesProcess returns mockExternalServicesProcesss

    val mockUserProcess = mock[UserProcess]

    mockInjector.userProcess returns mockUserProcess

    val mockUserAccountProcess = mock[UserAccountsProcess]

    mockInjector.userAccountsProcess returns mockUserAccountProcess

    val mockRecognitionProcess = mock[RecognitionProcess]

    mockInjector.recognitionProcess returns mockRecognitionProcess

    val mockObserverRegister = mock[ObserverRegister]

    mockInjector.observerRegister returns mockObserverRegister

    val mockMomentProcess = mock[MomentProcess]

    mockInjector.momentProcess returns mockMomentProcess

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val mockMomentPreferences = mock[MomentPreferences]

    val mockMomentBroadcastReceiver = mock[MomentBroadcastReceiver]

    val launcherJobs = new LauncherJobs(
      mockLauncherUiActions,
      mockWorkspaceUiActions,
      mockMenuDrawersUiActions,
      mockAppDrawerUiActions,
      mockNavigationUiActions,
      mockDockAppsUiActions,
      mockTopBarUiActionss,
      mockWidgetUiActions,
      mockDragUiActions) {

      override lazy val di: Injector = mockInjector

      override def sendBroadCastTask(broadAction: BroadAction) = TaskService.empty

      override def getThemeTask = TaskService.right(theme)

      override lazy val momentPreferences = mockMomentPreferences

      override def momentBroadcastReceiver = mockMomentBroadcastReceiver
    }
  }

}

class LauncherJobsSpec
  extends LauncherJobsSpecification {

  sequential
  "initialize" should {
    "Initialize all actions and services" in new LauncherJobsScope {

      mockLauncherUiActions.initialize() returns serviceRight(Unit)
      mockThemeProcess.getTheme(any)(any) returns serviceRight(theme)

      mockWidgetUiActions.initialize() returns serviceRight(Unit)
      mockMenuDrawersUiActions.initialize() returns serviceRight(Unit)
      mockAppDrawerUiActions.initialize() returns serviceRight(Unit)
      mockTopBarUiActionss.initialize() returns serviceRight(Unit)
      mockLauncherUiActions.initialize() returns serviceRight(Unit)

      mockExternalServicesProcesss.initializeStrictMode(any) returns serviceRight(Unit)
      mockExternalServicesProcesss.initializeCrashlytics(any) returns serviceRight(Unit)
      mockExternalServicesProcesss.initializeFirebase(any) returns serviceRight(Unit)
      mockExternalServicesProcesss.initializeStetho(any) returns serviceRight(Unit)

      mockUserProcess.register(any) returns serviceRight(Unit)

      launcherJobs.initialize().mustRightUnit
    }.pendingUntilFixed
  }

  "resume" should {
    "load the launcher if there aren't any collections" in new LauncherJobsScope {

      mockObserverRegister.registerObserverTask() returns serviceRight(Unit)
      mockLauncherDOM.isEmptyCollections returns true
      mockObserverRegister.registerObserverTask() returns serviceRight(Unit)
      mockRecognitionProcess.getWeather returns serviceRight(weatherState)
      mockWorkspaceUiActions.showWeather(any) returns serviceRight(Unit)

      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockDeviceProcess.getDockApps returns serviceRight(seqDockApp)
      mockMomentPreferences.getPersistMoment returns Option(HomeMorningMoment)
      mockMomentProcess.fetchMomentByType(any) returns serviceRight(Option(moment))

      mockUserProcess.getUser(any) returns serviceRight(user)
      mockMenuDrawersUiActions.loadUserProfileMenu(any, any, any, any) returns serviceRight(Unit)

      mockWorkspaceUiActions.loadLauncherInfo(any) returns serviceRight(Unit)
      mockDockAppsUiActions.loadDockApps(any) returns serviceRight(Unit)
      mockTopBarUiActionss.loadBar(any) returns serviceRight(Unit)
      mockMenuDrawersUiActions.reloadBarMoment(any) returns serviceRight(Unit)

      launcherJobs.resume().mustRightUnit

      there was one(mockObserverRegister).registerObserverTask()
    }.pendingUntilFixed

    "return a LoadDataException when loadLauncher " in new LauncherJobsScope {

      mockObserverRegister.registerObserverTask() returns serviceRight(Unit)
      mockLauncherDOM.isEmptyCollections returns true
      mockRecognitionProcess.getWeather returns serviceRight(weatherState)
      mockWorkspaceUiActions.showWeather(any) returns serviceRight(Unit)
      mockObserverRegister.registerObserverTask() returns serviceRight(Unit)

      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockDeviceProcess.getDockApps returns serviceRight(seqDockApp)
      mockMomentPreferences.getPersistMoment returns None
      mockMomentProcess.getBestAvailableMoment(any, any)(any) returns serviceRight(Option(moment))
      mockUserProcess.getUser(any) returns serviceLeft(UserException(""))

      launcherJobs.resume().mustLeft[LoadDataException]
      there was one(mockObserverRegister).registerObserverTask()
    }.pendingUntilFixed

    "call to changeMomentIfIsAvailable if has collections." in new LauncherJobsScope {

      mockObserverRegister.registerObserverTask() returns serviceRight(Unit)
      mockLauncherDOM.isEmptyCollections returns false
      mockMomentPreferences.nonPersist returns true
      mockMomentProcess.getBestAvailableMoment(any, any)(any) returns serviceRight(Option(moment))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockLauncherDOM.getCurrentMomentType returns Option(WorkMoment)
      mockWorkspaceUiActions.reloadMoment(any) returns serviceRight(Unit)
      mockObserverRegister.registerObserverTask() returns serviceRight(Unit)
      mockRecognitionProcess.getWeather returns serviceRight(weatherState)
      mockWorkspaceUiActions.showWeather(any) returns serviceRight(Unit)

      launcherJobs.resume().mustRightUnit

      there was one(mockObserverRegister).registerObserverTask()
    }.pendingUntilFixed

    "return a ChangeMomentException when change a moment" in new LauncherJobsScope {

      mockObserverRegister.registerObserverTask() returns serviceRight(Unit)
      mockLauncherDOM.isEmptyCollections returns false
      mockMomentProcess.getBestAvailableMoment(any,any)(any) returns serviceLeft(MomentException(""))
      mockObserverRegister.registerObserverTask() returns serviceRight(Unit)
      mockRecognitionProcess.getWeather returns serviceRight(weatherState)
      mockWorkspaceUiActions.showWeather(any) returns serviceRight(Unit)

      launcherJobs.resume().mustLeft[ChangeMomentException]

      there was one(mockObserverRegister).registerObserverTask()
    }.pendingUntilFixed
  }

  "registerFence" should {
    "register a fence" in new LauncherJobsScope {

      mockRecognitionProcess.registerFenceUpdates(any, any)(any) returns serviceRight(Unit)
      launcherJobs.registerFence().mustRightUnit
      there was one(mockRecognitionProcess).registerFenceUpdates(===(MomentBroadcastReceiver.momentFenceAction), any)(any)
    }
  }

  "unregisterFence" should {
    "unregister a fence" in new LauncherJobsScope {

      mockRecognitionProcess.unregisterFenceUpdates(any)(any) returns serviceRight(Unit)
      launcherJobs.unregisterFence().mustRightUnit
      there was one(mockRecognitionProcess).unregisterFenceUpdates(===(MomentBroadcastReceiver.momentFenceAction))(any)
    }
  }

  "reloadFence" should {
    "unregister and registers a fence" in new LauncherJobsScope {
      mockRecognitionProcess.unregisterFenceUpdates(any)(any) returns serviceRight(Unit)
      mockRecognitionProcess.registerFenceUpdates(any, any)(any) returns serviceRight(Unit)

      launcherJobs.reloadFence().mustRightUnit

      there was one(mockRecognitionProcess).unregisterFenceUpdates(any)(any)
      there was one(mockRecognitionProcess).registerFenceUpdates(any, any)(any)
    }
  }

  "pause" should {
    "call to unregisterObserverTask" in new LauncherJobsScope {

      mockObserverRegister.unregisterObserverTask() returns serviceRight(Unit)
      launcherJobs.pause().mustRightUnit
      there was one(mockObserverRegister).unregisterObserverTask()
    }
  }

  "destroy" should {
    "call to destroy" in new LauncherJobsScope {
      mockWidgetUiActions.destroy() returns serviceRight(Unit)
      launcherJobs.destroy().mustRightUnit
      there was one(mockWidgetUiActions).destroy()
    }
  }

  "reloadAppsMomentBar" should {
    "reload apps there is a moment associated with a collection" in new LauncherJobsScope {

      mockMomentProcess.getMoments returns serviceRight(seqMoment)
      mockLauncherDOM.getCurrentMomentType returns Option(HomeMorningMoment)
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockMenuDrawersUiActions.reloadBarMoment(any) returns serviceRight(Unit)

      launcherJobs.reloadAppsMomentBar().mustRightUnit

      there was one(mockMomentProcess).getMoments
      there was one(mockLauncherDOM).getCurrentMomentType
      there was one(mockCollectionProcess).getCollectionById(moment.collectionId.getOrElse(0))
      there was one(mockMenuDrawersUiActions).reloadBarMoment(LauncherMoment(Option(HomeMorningMoment), Option(collection)))
    }

    "reload apps when there isn't a moment associated with a collection" in new LauncherJobsScope {

      mockMomentProcess.getMoments returns serviceRight(seqMoment map (_.copy(collectionId = None)))
      mockLauncherDOM.getCurrentMomentType returns Option(HomeMorningMoment)
      mockMenuDrawersUiActions.reloadBarMoment(any) returns serviceRight(Unit)

      launcherJobs.reloadAppsMomentBar().mustRightUnit

      there was one(mockMomentProcess).getMoments
      there was one(mockLauncherDOM).getCurrentMomentType
      there was no(mockCollectionProcess).getCollectionById(any)
      there was one(mockMenuDrawersUiActions).reloadBarMoment(LauncherMoment(Option(HomeMorningMoment), None))
    }
  }

  "loadLauncherInfo" should {
    "load launcherInfo when the service returns a right response and there are collections" in new LauncherJobsScope {

      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockDeviceProcess.getDockApps returns serviceRight(seqDockApp)
      mockMomentPreferences.getPersistMoment returns Option(HomeMorningMoment)
      mockMomentProcess.fetchMomentByType(any) returns serviceRight(Option(moment))

      mockUserProcess.getUser(any) returns serviceRight(user)
      mockMenuDrawersUiActions.loadUserProfileMenu(any, any, any, any) returns serviceRight(Unit)

      mockWorkspaceUiActions.loadLauncherInfo(any) returns serviceRight(Unit)
      mockDockAppsUiActions.loadDockApps(any) returns serviceRight(Unit)
      mockTopBarUiActionss.loadBar(any) returns serviceRight(Unit)
      mockMenuDrawersUiActions.reloadBarMoment(any) returns serviceRight(Unit)

      launcherJobs.loadLauncherInfo().mustRightUnit

      there was one(mockCollectionProcess).getCollections
      there was one(mockDeviceProcess).getDockApps
      there was one(mockMomentPreferences).getPersistMoment
      there was one(mockMomentProcess).fetchMomentByType(HomeMorningMoment)
      there was one(mockUserProcess).getUser(any)
      there was one(mockMenuDrawersUiActions).loadUserProfileMenu(user.email, user.userProfile.name, user.userProfile.avatar, user.userProfile.cover)

    }

    "load launcherInfo when the service returns a right response, there are collections but there isn't a persist moment" in new LauncherJobsScope {

      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockDeviceProcess.getDockApps returns serviceRight(seqDockApp)
      mockMomentPreferences.getPersistMoment returns None
      mockMomentProcess.getBestAvailableMoment(any, any)(any) returns serviceRight(Option(moment))

      mockUserProcess.getUser(any) returns serviceRight(user)
      mockMenuDrawersUiActions.loadUserProfileMenu(any, any, any, any) returns serviceRight(Unit)

      mockWorkspaceUiActions.loadLauncherInfo(any) returns serviceRight(Unit)
      mockDockAppsUiActions.loadDockApps(any) returns serviceRight(Unit)
      mockTopBarUiActionss.loadBar(any) returns serviceRight(Unit)
      mockMenuDrawersUiActions.reloadBarMoment(any) returns serviceRight(Unit)

      launcherJobs.loadLauncherInfo().mustRightUnit

      there was one(mockCollectionProcess).getCollections
      there was one(mockDeviceProcess).getDockApps
      there was one(mockMomentPreferences).getPersistMoment
      there was no(mockMomentProcess).fetchMomentByType(HomeMorningMoment)
      there was one(mockMomentProcess).getBestAvailableMoment(any, any)(any)
      there was one(mockUserProcess).getUser(any)
      there was one(mockMenuDrawersUiActions).loadUserProfileMenu(user.email, user.userProfile.name, user.userProfile.avatar, user.userProfile.cover)

    }

    "load launcher and don't show the user info if UserService return an UserException" in new LauncherJobsScope {

      mockCollectionProcess.getCollections returns serviceRight(seqCollection)
      mockDeviceProcess.getDockApps returns serviceRight(seqDockApp)
      mockMomentPreferences.getPersistMoment returns None
      mockMomentProcess.getBestAvailableMoment(any, any)(any) returns serviceRight(Option(moment))
      mockUserProcess.getUser(any) returns serviceLeft(UserException(""))
      mockWorkspaceUiActions.loadLauncherInfo(any) returns serviceRight(Unit)
      mockDockAppsUiActions.loadDockApps(any) returns serviceRight(Unit)
      mockTopBarUiActionss.loadBar(any) returns serviceRight(Unit)
      mockMenuDrawersUiActions.reloadBarMoment(any) returns serviceRight(Unit)

      launcherJobs.loadLauncherInfo().mustRightUnit

      there was one(mockCollectionProcess).getCollections
      there was one(mockDeviceProcess).getDockApps
      there was one(mockMomentPreferences).getPersistMoment
      there was no(mockMomentProcess).fetchMomentByType(HomeMorningMoment)
      there was no(mockMenuDrawersUiActions).loadUserProfileMenu(any, any, any, any)
    }

    "return LoadDataException if there aren't any collections" in new LauncherJobsScope {

      mockCollectionProcess.getCollections returns serviceRight(Seq.empty)
      mockDeviceProcess.getDockApps returns serviceRight(seqDockApp)
      mockMomentPreferences.getPersistMoment returns Option(HomeMorningMoment)
      mockMomentProcess.fetchMomentByType(any) returns serviceRight(Option(moment))

      launcherJobs.loadLauncherInfo().mustLeft[LoadDataException]

      there was one(mockCollectionProcess).getCollections
      there was one(mockDeviceProcess).getDockApps
      there was one(mockMomentPreferences).getPersistMoment
      there was one(mockMomentProcess).fetchMomentByType(HomeMorningMoment)
    }
  }

  sequential
  "changeMomentIfIsAvailable" should {

    "Do nothing if the best Available Moment is equal to current moment" in new LauncherJobsScope {

      mockMomentPreferences.nonPersist returns true
      mockMomentProcess.getBestAvailableMoment(===(None), ===(None))(any) returns serviceRight(Option(moment))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockLauncherDOM.getCurrentMomentType returns Option(HomeMorningMoment)

      launcherJobs.changeMomentIfIsAvailable(true, None).mustRightUnit

      there was one(mockMomentProcess).getBestAvailableMoment(===(None), ===(None))(any)
    }

    "Reload moment if the best Available Moment isn't equal to current moment" in new LauncherJobsScope {

      mockMomentPreferences.nonPersist returns true
      mockMomentProcess.getBestAvailableMoment(any, any)(any) returns serviceRight(Option(moment))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockLauncherDOM.getCurrentMomentType returns Option(WorkMoment)
      mockWorkspaceUiActions.reloadMoment(any) returns serviceRight(Unit)

      launcherJobs.changeMomentIfIsAvailable(true, None).mustRightUnit

      there was one(mockMomentProcess).getBestAvailableMoment(===(None), ===(None))(any)
    }

    "Reload workspace with new date when haveHeadphonesFence" in new LauncherJobsScope {

      mockMomentPreferences.nonPersist returns true
      mockMomentProcess.getBestAvailableMoment(any, any)(any) returns serviceRight(Option(moment))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockLauncherDOM.getCurrentMomentType returns Option(WorkMoment)
      mockWorkspaceUiActions.reloadMoment(any) returns serviceRight(Unit)

      launcherJobs.changeMomentIfIsAvailable(false, Option(HeadphonesFence.keyIn)).mustRightUnit

      there was one(mockMomentProcess).getBestAvailableMoment(===(Option(true)), ===(None))(any)
    }

    "Reload workspace with new date when HeadphonesFence.keyOut" in new LauncherJobsScope {

      mockMomentPreferences.nonPersist returns true
      mockMomentProcess.getBestAvailableMoment(any, any)(any) returns serviceRight(Option(moment))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockLauncherDOM.getCurrentMomentType returns Option(WorkMoment)
      mockWorkspaceUiActions.reloadMoment(any) returns serviceRight(Unit)

      launcherJobs.changeMomentIfIsAvailable(false, Option(HeadphonesFence.keyOut)).mustRightUnit

      there was one(mockMomentProcess).getBestAvailableMoment(===(Option(false)), ===(None))(any)
    }

    "Reload workspace with new date when InVehicleFence.key" in new LauncherJobsScope {

      mockMomentPreferences.nonPersist returns true
      mockMomentProcess.getBestAvailableMoment(any, any)(any) returns serviceRight(Option(moment))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockLauncherDOM.getCurrentMomentType returns Option(WorkMoment)
      mockWorkspaceUiActions.reloadMoment(any) returns serviceRight(Unit)

      launcherJobs.changeMomentIfIsAvailable(false, Option(InVehicleFence.key)).mustRightUnit

      there was one(mockMomentProcess).getBestAvailableMoment(===(None), ===(Option(InVehicleActivity)))(any)
    }

  }

  "changeMoment" should {
    "change the current Moment with a collection associated" in new LauncherJobsScope {

      mockTrackEventProcess.changeMoment(any) returns serviceRight(Unit)
      mockMomentProcess.findMoment(any) returns serviceRight(Option(moment))
      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockWorkspaceUiActions.reloadMoment(any) returns serviceRight(Unit)

      launcherJobs.changeMoment(moment.id).mustRightUnit

      there was one(mockTrackEventProcess).changeMoment(moment.momentType.name)
      there was one(mockMomentProcess).findMoment(moment.id)
      there was one(mockCollectionProcess).getCollectionById(moment.collectionId.getOrElse(0))
      there was one(mockWorkspaceUiActions).reloadMoment(LauncherData(MomentWorkSpace, Option(LauncherMoment(Option(moment.momentType), Option(collection)))))
    }

    "change the Moment without a collection associated" in new LauncherJobsScope {

      mockTrackEventProcess.changeMoment(any) returns serviceRight(Unit)
      mockMomentProcess.findMoment(any) returns serviceRight(Option(moment.copy(collectionId = None)))
      mockWorkspaceUiActions.reloadMoment(any) returns serviceRight(Unit)

      launcherJobs.changeMoment(moment.id).mustRightUnit

      there was one(mockTrackEventProcess).changeMoment(moment.momentType.name)
      there was one(mockMomentProcess).findMoment(moment.id)
      there was no(mockCollectionProcess).getCollectionById(any)
      there was one(mockWorkspaceUiActions).reloadMoment(LauncherData(MomentWorkSpace, Option(LauncherMoment(Option(moment.momentType), None))))
    }

    "return an exception when the moment with momentId has not been found" in new LauncherJobsScope {

      mockTrackEventProcess.changeMoment(any) returns serviceRight(Unit)
      mockMomentProcess.findMoment(any) returns serviceLeft(MomentException(""))
      launcherJobs.changeMoment(moment.id).mustLeft[MomentException]
      there was no(mockTrackEventProcess).changeMoment(moment.momentType.name)
      there was one(mockMomentProcess).findMoment(moment.id)
    }
  }

  "cleanPersistedMoment" should {
    "call to clean" in new LauncherJobsScope {

      mockTrackEventProcess.unpinMoment() returns serviceRight(Unit)
      launcherJobs.cleanPersistedMoment().mustRightUnit
      there was one(mockTrackEventProcess).unpinMoment()
      there was one(mockMomentPreferences).clean()
    }
  }

  "reloadCollection" should {
    "Reload a collection with a valid collectionId" in new LauncherJobsScope {

      mockCollectionProcess.getCollectionById(any) returns serviceRight(Option(collection))
      mockLauncherDOM.getData returns seqLauncherData.map(_.copy(collections = seqCollection, workSpaceType = CollectionsWorkSpace))
      mockWorkspaceUiActions.reloadWorkspaces(any, any) returns serviceRight(Unit)

      launcherJobs.reloadCollection(collection.id).mustRightUnit

      there was one(mockCollectionProcess).getCollectionById(collection.id)
    }
  }

  "addCollection" should {
    "Add the collection when workSpaceType is CollectionsWorkSpace" in new LauncherJobsScope {

      mockLauncherDOM.getData returns seqLauncherData.map(_.copy(collections = seqCollection, workSpaceType = CollectionsWorkSpace))
      mockWorkspaceUiActions.reloadWorkspaces(any, any) returns serviceRight(Unit)

      launcherJobs.addCollection(collection).mustRightUnit

    }
    "do nothing when workSpaceType isn't CollectionsWorkSpace" in new LauncherJobsScope {

      mockLauncherDOM.getData returns seqLauncherData.map(_.copy(collections = seqCollection))
      launcherJobs.addCollection(collection).mustRightUnit

    }
  }

  "updateCollection" should {
    "update a collection when the service returns a right response" in new LauncherJobsScope {

      mockLauncherDOM.getData returns seqLauncherData.map(_.copy(collections = seqCollection, workSpaceType = CollectionsWorkSpace))
      mockWorkspaceUiActions.reloadWorkspaces(any, any) returns serviceRight(Unit)

      launcherJobs.updateCollection(collection).mustRightUnit

      there was one(mockWorkspaceUiActions).reloadWorkspaces(any, any)
    }

    "update the collection when there are collections but a collection with this position has not been found" in new LauncherJobsScope {

      mockLauncherDOM.getData returns seqLauncherData.map(_.copy(collections = seqCollection, workSpaceType = CollectionsWorkSpace))
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)

      launcherJobs.updateCollection(collection.copy(position = 30)).mustRightUnit

      there was no(mockWorkspaceUiActions).reloadWorkspaces(any, any)
    }

    "Do nothing when there aren't any collections" in new LauncherJobsScope {

      mockLauncherDOM.getData returns seqLauncherData.map(_.copy(collections = Seq.empty, workSpaceType = CollectionsWorkSpace))
      mockNavigationUiActions.showContactUsError() returns serviceRight(Unit)

      launcherJobs.updateCollection(collection).mustRightUnit

      there was no(mockWorkspaceUiActions).reloadWorkspaces(any, any)
    }
  }

  "removeCollection" should {
    "Do nothing if workSpaceType isn't CollectionsWorkSpace" in new LauncherJobsScope {

      mockTrackEventProcess.deleteCollection(any) returns serviceRight(Unit)
      mockCollectionProcess.deleteCollection(any) returns serviceRight(Unit)
      mockLauncherDOM.getData returns seqLauncherData.map(_.copy(collections = seqCollection))
      mockWorkspaceUiActions.reloadWorkspaces(any, any) returns serviceRight(Unit)

      launcherJobs.removeCollection(collection).mustRightUnit

      there was one(mockTrackEventProcess).deleteCollection(collection.name)
      there was one(mockCollectionProcess).deleteCollection(collection.id)
      there was one(mockWorkspaceUiActions).reloadWorkspaces(Seq.empty, Option(launcherJobs.defaultPage))

    }

    "Remove the collection" in new LauncherJobsScope {

      mockTrackEventProcess.deleteCollection(any) returns serviceRight(Unit)
      mockCollectionProcess.deleteCollection(any) returns serviceRight(Unit)
      mockLauncherDOM.getData returns seqLauncherData.map(_.copy(collections = seqCollection, workSpaceType = CollectionsWorkSpace))
      mockWorkspaceUiActions.reloadWorkspaces(any, any) returns serviceRight(Unit)

      launcherJobs.removeCollection(collection).mustRightUnit

      there was one(mockTrackEventProcess).deleteCollection(collection.name)
      there was one(mockCollectionProcess).deleteCollection(collection.id)
    }
  }

  "removeMomentDialog" should {
    "Show a  message indicating it can't remove the moment OutAndAboutMoment" in new LauncherJobsScope {

      mockNavigationUiActions.showCantRemoveOutAndAboutMessage() returns serviceRight(Unit)
      launcherJobs.removeMomentDialog(OutAndAboutMoment, moment.id).mustRightUnit
      there was one(mockNavigationUiActions).showCantRemoveOutAndAboutMessage()

    }

    "Show a message for remove moment" in new LauncherJobsScope {

      mockNavigationUiActions.showDialogForRemoveMoment(any) returns serviceRight(Unit)
      launcherJobs.removeMomentDialog(moment.momentType, moment.id).mustRightUnit
      there was one(mockNavigationUiActions).showDialogForRemoveMoment(moment.id)
    }
  }

  "removeMoment" should {
    "return a valid response when the service returns a right response" in new LauncherJobsScope {

      mockTrackEventProcess.deleteMoment() returns serviceRight(Unit)
      mockTrackEventProcess.unpinMoment() returns serviceRight(Unit)
      mockMomentProcess.deleteMoment(any) returns serviceRight(Unit)
      mockRecognitionProcess.unregisterFenceUpdates(any)(any) returns serviceRight(Unit)
      mockRecognitionProcess.registerFenceUpdates(any, any)(any) returns serviceRight(Unit)

      launcherJobs.removeMoment(moment.id).mustRightUnit

      there was one(mockTrackEventProcess).unpinMoment()
      there was one(mockTrackEventProcess).deleteMoment()
      there was one(mockMomentProcess).deleteMoment(moment.id)
    }
  }

  sequential
  "requestPermissionsResult" should {
    "call to reloadContacts for the specified permissions: contactsPermission" in new LauncherJobsScope {

      mockUserAccountProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(ReadContacts, result = true)))
      mockAppDrawerUiActions.reloadContacts() returns serviceRight(Unit)

      launcherJobs.requestPermissionsResult(RequestCodes.contactsPermission,Array(GetAccounts.value, ReadContacts.value), Array.empty).mustRightUnit
    }
    "call to reloadContacts for the specified permissions: callLogPermission" in new LauncherJobsScope {

      mockUserAccountProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(ReadCallLog, result = true)))
      mockAppDrawerUiActions.reloadContacts() returns serviceRight(Unit)

      launcherJobs.requestPermissionsResult(RequestCodes.callLogPermission,Array(ReadCallLog.value), Array.empty).mustRightUnit
    }
    "call to launcherExecutorProcess for the specified permissions: phoneCallPermission " in new LauncherJobsScope {

      mockUserAccountProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(CallPhone, result = true)))
      statuses = statuses.copy(lastPhone = Option(lastPhone))
      mockLauncherExecutorProcess.execute(any)(any) returns serviceRight(Unit)

      launcherJobs.requestPermissionsResult(RequestCodes.phoneCallPermission,Array(CallPhone.value), Array.empty).mustRightUnit
    }

    "Does nothing for the specified permissions :phoneCallPermission if hasn't lastPhone" in new LauncherJobsScope {

      mockUserAccountProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(CallPhone, result = true)))
      statuses = statuses.copy(lastPhone = None)
      launcherJobs.requestPermissionsResult(RequestCodes.phoneCallPermission,Array(CallPhone.value), Array.empty).mustRightUnit
    }

    "Show a message error and try to request the permission with contactsPermission" in new LauncherJobsScope {

      mockUserAccountProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(ReadContacts, result = false)))
      mockAppDrawerUiActions.reloadApps() returns serviceRight(Unit)
      mockNavigationUiActions.showContactPermissionError(any) returns serviceRight(Unit)
      mockUserAccountProcess.requestPermission(any,any)(any) returns serviceRight(Unit)

      launcherJobs.requestPermissionsResult(RequestCodes.contactsPermission,Array(GetAccounts.value, ReadContacts.value), Array.empty).mustRightUnit
    }

    "Show a message error and try to request the permission with callLogPermission" in new LauncherJobsScope {

      mockUserAccountProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(ReadCallLog, result = false)))
      mockAppDrawerUiActions.reloadApps() returns serviceRight(Unit)
      mockNavigationUiActions.showCallPermissionError(any) returns serviceRight(Unit)
      mockUserAccountProcess.requestPermission(any,any)(any) returns serviceRight(Unit)

      launcherJobs.requestPermissionsResult(RequestCodes.callLogPermission,Array(ReadCallLog.value), Array.empty).mustRightUnit
    }

    "Show a message error if haven't permissions phoneCallPermission " in new LauncherJobsScope {

      mockUserAccountProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(CallPhone, result = false)))
      statuses = statuses.copy(lastPhone = Option(lastPhone))
      mockLauncherExecutorProcess.launchDial(any)(any) returns serviceRight(Unit)
      mockNavigationUiActions.showNoPhoneCallPermissionError() returns serviceRight(Unit)

      launcherJobs.requestPermissionsResult(RequestCodes.phoneCallPermission,Array(CallPhone.value), Array.empty).mustRightUnit
    }

    "Do nothing for the specified permissions :phoneCallPermission if hasn't lastPhone " in new LauncherJobsScope {

      mockUserAccountProcess.parsePermissionsRequestResult(any,any) returns serviceRight(Seq(PermissionResult(CallPhone, result = false)))
      statuses = statuses.copy(lastPhone = None)

      launcherJobs.requestPermissionsResult(RequestCodes.phoneCallPermission,Array(CallPhone.value), Array.empty).mustRightUnit
    }
  }
}