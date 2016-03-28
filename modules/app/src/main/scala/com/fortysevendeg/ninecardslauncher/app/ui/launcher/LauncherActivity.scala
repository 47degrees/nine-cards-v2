package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.fortysevendeg.ninecardslauncher.app.analytics._
import com.fortysevendeg.ninecardslauncher.app.commons.{ContextSupportProvider, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.ActionsScreenListener
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid._
import rapture.core.Answer

import scalaz.concurrent.Task

class LauncherActivity
  extends AppCompatActivity
  with Contexts[AppCompatActivity]
  with ContextSupportProvider
  with TypedFindView
  with ActionsScreenListener
  with LauncherComposer
  with LauncherTasks
  with SystemBarsTint
  with NineCardIntentConversions
  with AnalyticDispatcher
  with DrawerListeners { self =>

  implicit lazy val di: Injector = new Injector

  implicit lazy val uiContext: UiContext[Activity] = ActivityUiContext(this)

  implicit lazy val theme: NineCardsTheme = di.themeProcess.getSelectedTheme.run.run match {
    case Answer(t) => t
    case _ => getDefaultTheme
  }

  val tagDialog = "dialog"

  var hasFocus = false

  var userProfileStatuses = UserProfileStatuses()

  val clickListener: (App) => Unit = (app: App) => {
    if (isTabsOpened) {
      closeTabs.run
    } else {
      self !>>
        TrackEvent(
          screen = CollectionDetailScreen,
          category = AppCategory(app.category),
          action = OpenAction,
          label = Some(ProvideLabel(app.packageName)),
          value = Some(OpenAppFromAppDrawerValue))
      execute(toNineCardIntent(app))
    }
  }

  val longClickListener: (App) => Unit = (app: App) => {
    if (isTabsOpened) {
      closeTabs.run
    } else {
      launchSettings(app.packageName)
    }
  }

  override def onCreate(bundle: Bundle) = {
    super.onCreate(bundle)
    Task.fork(di.userProcess.register.run).resolveAsync()
    setContentView(R.layout.launcher_activity)
    initUi.run
    initAllSystemBarsTint
  }

  override def onResume(): Unit = {
    super.onResume()
    if (isEmptyCollections) {
      loadCollectionsAndDockApps()
    }
  }

  override def onStartFinishAction(): Unit = turnOffFragmentContent.run

  override def onEndFinishAction(): Unit = removeActionFragment

  override def onBackPressed(): Unit = backByPriority.run

  override def onWindowFocusChanged(hasFocus: Boolean): Unit = {
    super.onWindowFocusChanged(hasFocus)
    this.hasFocus = hasFocus
  }

  override def onNewIntent(intent: Intent): Unit = {
    super.onNewIntent(intent)
    val alreadyOnHome = hasFocus && ((intent.getFlags &
      Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
      != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
    if (alreadyOnHome) backByPriority.run
  }

  override def dispatchKeyEvent(event: KeyEvent): Boolean = (event.getAction, event.getKeyCode) match {
    case (KeyEvent.ACTION_DOWN | KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME) => true
    case _ => super.dispatchKeyEvent(event)
  }

  def addCollection(collection: Collection): Unit = uiActionCollection(Add, collection).run

  def removeCollection(collection: Collection): Unit = {
    val overOneCollection = workspaces.exists(_.data.filterNot(_.workSpaceType.isMomentWorkSpace).headOption.exists(_.collections.length!=1))
    if (overOneCollection) {
      val ft = getSupportFragmentManager.beginTransaction()
      Option(getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
      ft.addToBackStack(javaNull)
      val dialog = new RemoveCollectionDialogFragment(() => {
        Task.fork(di.collectionProcess.deleteCollection(collection.id).run).resolveAsyncUi(
          onResult = (_) => uiActionCollection(Remove, collection),
          onException = (_) => showMessage(R.string.contactUsError)
        )
      })
      dialog.show(ft, tagDialog)
    } else {
      showMessage(R.string.minimumOneCollectionMessage).run
    }
  }

  private[this] def loadCollectionsAndDockApps(): Unit = Task.fork(getLauncherApps.run).resolveAsyncUi(
    onResult = {
      // Check if there are collections in DB, if there aren't we go to wizard
      case (Nil, _) => goToWizard()
      case (collections, apps) =>
        loadUserProfile()
        createCollections(collections, apps)
    },
    onException = (ex: Throwable) => goToWizard(),
    onPreTask = () => showLoading
  )

  def onConnectedUserProfile(name: String, email: String, avatarUrl: Option[String]): Unit = userProfileMenu(name, email, avatarUrl).run

  def onConnectedPlusProfile(coverPhotoUrl: String): Unit = plusProfileMenu(coverPhotoUrl).run

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    userProfileStatuses.userProfile foreach (_.connectUserProfile(requestCode, resultCode, data))
    (requestCode, resultCode) match {
      case (RequestCodes.goToProfile, ResultCodes.logoutSuccessful) =>
        ((workspaces <~ lwsClean) ~ goToWizard()).run
      case _ =>
    }
  }

  private[this] def goToWizard(): Ui[_] = Ui {
    val wizardIntent = new Intent(LauncherActivity.this, classOf[WizardActivity])
    startActivity(wizardIntent)
  }

  private[this] def toGetAppOrder(appsMenuOption: AppsMenuOption): GetAppOrder = appsMenuOption match {
    case AppsAlphabetical => GetByName
    case AppsByCategories => GetByCategory
    case AppsByLastInstall => GetByInstallDate
  }

  private[this] def toGetContactFilter(contactMenuOption: ContactsMenuOption): ContactsFilter = contactMenuOption match {
    case ContactsFavorites => FavoriteContacts
    case _ => AllContacts
  }

  override def loadApps(appsMenuOption: AppsMenuOption): Unit = {
    val getAppOrder = toGetAppOrder(appsMenuOption)
    Task.fork(getLoadApps(getAppOrder).run).resolveAsyncUi(
      onResult = {
        case (apps: IterableApps, counters: Seq[TermCounter]) =>
          addApps(
            apps = apps,
            clickListener = clickListener,
            longClickListener = longClickListener,
            getAppOrder = getAppOrder,
            counters = counters)
      }
    )
  }

  override def loadContacts(contactsMenuOption: ContactsMenuOption): Unit =
    contactsMenuOption match {
      case ContactsByLastCall =>
        Task.fork(di.deviceProcess.getLastCalls.run).resolveAsyncUi(
          onResult = (contacts: Seq[LastCallsContact]) => addLastCallContacts(contacts, (contact: LastCallsContact) => {
            if (isTabsOpened) {
              closeTabs.run
            } else {
              execute(phoneToNineCardIntent(contact.number))
            }
          }))
      case _ =>
        val getContactFilter = toGetContactFilter(contactsMenuOption)
        Task.fork(getLoadContacts(getContactFilter).run).resolveAsyncUi(
          onResult = {
            case (contacts: IterableContacts, counters: Seq[TermCounter]) =>
              addContacts(
                contacts = contacts,
                clickListener = (contact: Contact) => {
                  if (isTabsOpened) {
                    closeTabs.run
                  } else {
                    executeContact(contact.lookupKey)
                  }
                },
                counters = counters)
          })
    }

  override def loadAppsByKeyword(keyword: String): Unit =
    Task.fork(di.deviceProcess.getIterableAppsByKeyWord(keyword, GetByName).run).resolveAsyncUi(
      onResult = {
        case (apps: IterableApps) =>
          addApps(
            apps = apps,
            clickListener = clickListener,
            longClickListener = longClickListener)
      })

  override def loadContactsByKeyword(keyword: String): Unit =
    Task.fork(di.deviceProcess.getIterableContactsByKeyWord(keyword).run).resolveAsyncUi(
      onResult = {
        case (contacts: IterableContacts) =>
          addContacts(
            contacts = contacts,
            clickListener = (contact: Contact) => {
              executeContact(contact.lookupKey)
            })
      })

  private[this] def loadUserProfile(): Unit =
    Task.fork(di.userProcess.getUser.run).resolveAsyncUi(
      onResult = user => Ui {
        val userProfile = user.email map { email =>
          new UserProfileProvider(
            account = email,
            onConnectedUserProfile = onConnectedUserProfile,
            onConnectedPlusProfile = onConnectedPlusProfile)
        }
        userProfileStatuses = userProfileStatuses.copy(userProfile = userProfile)
        userProfileStatuses.userProfile foreach (_.connect())
      })

}
