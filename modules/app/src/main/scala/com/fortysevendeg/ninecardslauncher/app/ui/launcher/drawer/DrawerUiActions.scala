package com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer

import java.io.Closeable

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.LayoutManager
import android.view.View
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SystemBarsTint
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.apps.AppsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.contacts.{ContactsAdapter, LastCallsAdapter}
import com.fortysevendeg.ninecardslauncher.app.ui.components.commons.SelectedItemDecoration
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.snails.TabsSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToDownViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.PullToTabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.SearchBoxesViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.SwipeAnimatedDrawerViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.TabsViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.DrawerRecyclerViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherUiActionsImpl
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer.DrawerSnails._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons._
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Try}

trait DrawerUiActions
  extends DrawerStyles
  with ContextSupportProvider
  with PullToTabsViewStyles {

  self: TypedFindView with SystemBarsTint with Contexts[AppCompatActivity] with LauncherUiActionsImpl =>

  val pages = 2

  val resistance = 2.4f

  lazy val appDrawerMain = Option(findView(TR.launcher_app_drawer))

  lazy val drawerContent = Option(findView(TR.launcher_drawer_content))

  lazy val scrollerLayout = findView(TR.launcher_drawer_scroller_layout)

  lazy val paginationDrawerPanel = Option(findView(TR.launcher_drawer_pagination_panel))

  lazy val recycler = Option(findView(TR.launcher_drawer_recycler))

  lazy val tabs = Option(findView(TR.launcher_drawer_tabs))

  lazy val pullToTabsView = Option(findView(TR.launcher_drawer_pull_to_tabs))

  lazy val screenAnimation = Option(findView(TR.launcher_drawer_swipe_animated))

  lazy val searchBoxView = Option(findView(TR.launcher_search_box_content))

  lazy val appTabs = AppsMenuOption.list map {
    case AppsAlphabetical => TabInfo(R.drawable.app_drawer_filter_alphabetical, resGetString(R.string.apps_alphabetical))
    case AppsByCategories => TabInfo(R.drawable.app_drawer_filter_categories, resGetString(R.string.apps_categories))
    case AppsByLastInstall => TabInfo(R.drawable.app_drawer_filter_installation_date, resGetString(R.string.apps_date))
  }

  lazy val contactsTabs = ContactsMenuOption.list map {
    case ContactsAlphabetical => TabInfo(R.drawable.app_drawer_filter_alphabetical, resGetString(R.string.contacts_alphabetical))
    case ContactsFavorites => TabInfo(R.drawable.app_drawer_filter_favorites, resGetString(R.string.contacts_favorites))
    case ContactsByLastCall => TabInfo(R.drawable.app_drawer_filter_last_call, resGetString(R.string.contacts_last))
  }

  def showGeneralError: Ui[_] = drawerContent <~ vSnackbarShort(R.string.contactUsError)

  def initDrawerUi: Ui[_] = {
    val selectItemsInScrolling = AppDrawerSelectItemsInScroller.readValue(preferenceValues)
    (searchBoxView <~
      sbvUpdateContentView(AppsView) <~
      sbvChangeListener(SearchBoxAnimatedListener(
        onHeaderIconClick = () => (if (isDrawerTabsOpened) closeDrawerTabs else openTabs).run,
        onAppStoreIconClick = () => presenter.launchPlayStore,
        onContactsIconClick = () => presenter.launchDial()
      )) <~
      sbvOnChangeText((text: String) => {
        (text, getStatus, getTypeView) match {
          case ("", Some(status), Some(AppsView)) =>
            AppsMenuOption(status) foreach (option => presenter.loadApps(option))
          case ("", Some(status), Some(ContactView)) =>
            ContactsMenuOption(status) foreach (option => presenter.loadContacts(option))
          case (t, _, Some(AppsView)) => presenter.loadAppsByKeyword(t)
          case (t, _, Some(ContactView)) => presenter.loadContactsByKeyword(t)
          case _ =>
        }
      })) ~
      (tabs <~ tvClose) ~
      (appDrawerMain <~
        appDrawerMainStyle <~
        On.click (openDrawer(longClick = false)) <~
        On.longClick (openDrawer(longClick = true) ~ Ui(true))) ~
      (recycler <~
        recyclerStyle <~
        drvListener(DrawerRecyclerViewListener(
          start = startMovementAppsContacts,
          move = moveMovementAppsContacts,
          end = endMovementAppsContacts,
          changeContentView = changeContentView
        )) <~
        (if (selectItemsInScrolling) rvAddItemDecoration(new SelectedItemDecoration) else Tweak.blank)) ~
      (scrollerLayout <~ scrollableStyle) ~
      (pullToTabsView <~
        pdvHorizontalEnable(true) <~
        (recycler map (rv => pdvHorizontalListener(rv.horizontalMovementListener)) getOrElse Tweak.blank) <~
        ptvLinkTabs(
          tabs = tabs,
          start = Ui.nop,
          end = Ui.nop) <~
        ptvAddTabsAndActivate(appTabs, 0, None) <~
        pdvResistance(resistance) <~
        ptvListener(PullToTabsListener(
          changeItem = (pos: Int) => {
            ((getTypeView match {
              case Some(AppsView) =>
                AppsMenuOption.list lift pos map loadAppsAndSaveStatus getOrElse Ui.nop
              case Some(ContactView) =>
                ContactsMenuOption.list lift pos map loadContactsAndSaveStatus getOrElse Ui.nop
              case _ => Ui.nop
            }) ~ (if (isDrawerTabsOpened) closeDrawerTabs else Ui.nop) ~ (searchBoxView <~ sbvClean)).run
          }
        ))) ~
      (drawerContent <~ contentStyle) ~
      loadAppsAlphabetical ~
      createDrawerPagers
  }

  private[this] def openDrawer(longClick: Boolean) = {
    val loadContacts = AppDrawerLongPressAction.readValue(preferenceValues) == AppDrawerLongPressActionOpenContacts && longClick
    (if (loadContacts) {
      Ui(
        recycler foreach { _.getAdapter match {
          case a: AppsAdapter => a.clear()
          case _ =>
        }}) ~ loadContactsAlphabetical
    } else if (getItemsCount == 0) {
      loadAppsAlphabetical
    } else {
      Ui.nop
    }) ~ revealInDrawer(longClick) ~~ (topBarPanel <~ vGone)
  }

  protected def openTabs: Ui[_] =
    (tabs <~ tvOpen <~ showTabs) ~
      (recycler <~ hideList)

  protected def closeDrawerTabs: Ui[_] =
    (tabs <~ tvClose <~ hideTabs) ~
      (recycler <~ showList)

  private[this] def closeCursorAdapter: Ui[_] = {

    def safeClose(closeable: Closeable): Unit = Try(closeable.close()) match {
      case Failure(ex) => printErrorMessage(ex)
      case _ =>
    }

    Ui(
      recycler foreach { _.getAdapter match {
        case a: Closeable => safeClose(a)
        case _ =>
      }})
  }

  private[this] def loadAppsAndSaveStatus(option: AppsMenuOption): Ui[_] = {
    val maybeDrawable = appTabs.lift(AppsMenuOption(option)) map (_.drawable)
    presenter.loadApps(option)
    (searchBoxView <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank)) ~
      (recycler <~ drvSetType(option))
  }

  private[this] def loadContactsAndSaveStatus(option: ContactsMenuOption): Ui[_] = {
    val maybeDrawable = contactsTabs.lift(ContactsMenuOption(option)) map (_.drawable)
    presenter.loadContacts(option)
    (searchBoxView <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank)) ~
      (recycler <~ drvSetType(option))
  }

  private[this] def loadAppsAlphabetical: Ui[_] = {
    val maybeDrawable = contactsTabs.lift(ContactsMenuOption(ContactsAlphabetical)) map (_.drawable)
    loadAppsAndSaveStatus(AppsAlphabetical) ~
      (paginationDrawerPanel <~ reloadPager(0)) ~
      (pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(appTabs, 0, None)) ~
      (searchBoxView <~ sbvUpdateContentView(AppsView) <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank))
  }

  private[this] def loadContactsAlphabetical: Ui[_] = {
    val maybeDrawable = appTabs.lift(AppsMenuOption(AppsAlphabetical)) map (_.drawable)
    val favoriteContactsFirst = AppDrawerFavoriteContactsFirst.readValue(preferenceValues)
    loadContactsAndSaveStatus(if (favoriteContactsFirst) ContactsFavorites else ContactsAlphabetical) ~
      (paginationDrawerPanel <~ reloadPager(1)) ~
      (pullToTabsView <~
        ptvClearTabs() <~
        ptvAddTabsAndActivate(contactsTabs, if (favoriteContactsFirst) 1 else 0, None)) ~
      (searchBoxView <~ sbvUpdateContentView(ContactView) <~ (maybeDrawable map sbvUpdateHeaderIcon getOrElse Tweak.blank))
  }

  private[this] def startMovementAppsContacts(): Ui[_] =
    (pullToTabsView <~ pdvEnable(false)) ~
      (screenAnimation <~
        (getTypeView map (cv => sadvInitAnimation(cv, getDrawerWidth)) getOrElse Tweak.blank))

  private[this] def moveMovementAppsContacts(displacement: Float): Ui[_] =
    screenAnimation <~
      (getTypeView map (cv => sadvMoveAnimation(cv, getDrawerWidth, displacement)) getOrElse Tweak.blank)

  private[this] def endMovementAppsContacts(duration: Int): Ui[_] =
    (pullToTabsView <~ pdvEnable(true)) ~
      (screenAnimation <~ sadvEndAnimation(duration))

  private[this] def changeContentView(contentView: ContentView): Ui[_] =
    (searchBoxView <~ sbvClean) ~
      closeCursorAdapter ~
      (contentView match {
        case AppsView => loadAppsAlphabetical
        case ContactView => loadContactsAlphabetical
      })

  private[this] def getDrawerWidth: Int = drawerContent map (dc => dc.getWidth) getOrElse 0

  def isDrawerVisible = drawerContent exists (_.getVisibility == View.VISIBLE)

  def revealInDrawer(longClick: Boolean): Ui[Future[_]] = {
    val showKeyboard = AppDrawerLongPressAction.readValue(preferenceValues) == AppDrawerLongPressActionOpenKeyboard && longClick
    (drawerLayout <~ dlLockedClosedStart <~ dlLockedClosedEnd) ~
      (paginationDrawerPanel <~ reloadPager(0)) ~
      (appDrawerMain mapUiF { source =>
        (drawerContent <~~
          openAppDrawer(AppDrawerAnimation.readValue(preferenceValues), source)) ~~
          (searchBoxView <~
            sbvEnableSearch <~
            (if (showKeyboard) sbvShowKeyboard else Tweak.blank))
      })
  }

  def revealOutDrawer: Ui[_] = {
    val collectionMoment = getData.headOption flatMap (_.moment) flatMap (_.collection)
    val searchIsEmpty = searchBoxView exists (_.isEmpty)
    (drawerLayout <~ dlUnlockedStart <~ (if (collectionMoment.isDefined) dlUnlockedEnd else Tweak.blank)) ~
      (topBarPanel <~ vVisible) ~
      (searchBoxView <~ sbvClean <~ sbvDisableSearch) ~
      (appDrawerMain mapUiF (source => (drawerContent <~~ closeAppDrawer(AppDrawerAnimation.readValue(preferenceValues), source)) ~~ resetData(searchIsEmpty)))
  }

  def addApps(
    apps: IterableApps,
    clickListener: (App) => Unit,
    longClickListener: (View, App) => Unit,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): Ui[_] = {
    val appsAdapter = AppsAdapter(
      apps = apps,
      clickListener = clickListener,
      longClickListener = Option(longClickListener))
    swipeAdapter(
      adapter = appsAdapter,
      layoutManager = appsAdapter.getLayoutManager,
      counters = counters,
      signalType = getAppOrder match {
        case GetByInstallDate => FastScrollerInstallationDate
        case GetByCategory => FastScrollerCategory
        case _ => FastScrollerText
      })
  }

  protected def isDrawerTabsOpened: Boolean = (tabs ~> isOpened).get getOrElse false

  private[this] def getStatus: Option[String] = recycler flatMap (rv => rv.getType)

  private[this] def getTypeView: Option[ContentView] = recycler map (_.statuses.contentView)

  private[this] def getItemsCount: Int = (for {
    rv <- recycler
    adapter <- Option(rv.getAdapter)
  } yield adapter.getItemCount) getOrElse 0

  def paginationDrawer(position: Int) =
    (w[ImageView] <~ paginationDrawerItemStyle <~ vSetPosition(position)).get

  private[this] def createDrawerPagers = {
    val pagerViews = 0 until pages map paginationDrawer
    paginationDrawerPanel <~ vgAddViews(pagerViews)
  }

  private[this] def resetData(searchIsEmpty: Boolean) =
    if (searchIsEmpty && isShowingAppsAlphabetical) {
      (recycler <~ rvScrollToTop) ~ (scrollerLayout <~ fslReset)
    } else {
      closeCursorAdapter ~ loadAppsAlphabetical ~ (searchBoxView <~ sbvUpdateContentView(AppsView))
    }

  private[this] def isShowingAppsAlphabetical = recycler exists (_.isType(AppsAlphabetical.name))

  def addContacts(
    contacts: IterableContacts,
    clickListener: (Contact) => Unit,
    longClickListener: (View, Contact) => Unit,
    counters: Seq[TermCounter] = Seq.empty): Ui[_] = {
    val contactAdapter = ContactsAdapter(
      contacts = contacts,
      clickListener = clickListener,
      longClickListener = Some(longClickListener))
    swipeAdapter(
      contactAdapter,
      contactAdapter.getLayoutManager,
      counters)
  }

  def addLastCallContacts(contacts: Seq[LastCallsContact], clickListener: (LastCallsContact) => Unit): Ui[_] = {
    val contactAdapter = LastCallsAdapter(
      contacts = contacts,
      clickListener = clickListener)
    swipeAdapter(
      contactAdapter,
      contactAdapter.getLayoutManager,
      Seq.empty)
  }

  private[this] def swipeAdapter(
    adapter: RecyclerView.Adapter[_],
    layoutManager: LayoutManager,
    counters: Seq[TermCounter],
    signalType: FastScrollerSignalType = FastScrollerText) = {
    val searchIsEmpty = searchBoxView exists (_.isEmpty)
    val lastTimeContentViewWasChanged = recycler exists (_.statuses.lastTimeContentViewWasChanged)
    val addFieldTweaks = getTypeView map {
      case AppsView => vAddField(SelectedItemDecoration.showLine, true)
      case ContactView => vAddField(SelectedItemDecoration.showLine, false)
    } getOrElse Tweak.blank
    closeCursorAdapter ~
      (recycler <~
        rvLayoutManager(layoutManager) <~
        (if (searchIsEmpty && !lastTimeContentViewWasChanged) rvLayoutAnimation(R.anim.list_slide_in_bottom_animation) else Tweak.blank) <~
        addFieldTweaks <~
        rvAdapter(adapter) <~
        rvScrollToTop) ~
      scrollerLayoutUi(counters, signalType)
  }

  private[this] def scrollerLayoutUi(counters: Seq[TermCounter], signalType: FastScrollerSignalType): Ui[_] =
    recycler map { rv =>
      scrollerLayout <~ fslEnabledScroller(true) <~ fslLinkRecycler(rv) <~ fslReset <~ fslCounters(counters) <~ fslSignalType(signalType)
    } getOrElse showGeneralError

}
