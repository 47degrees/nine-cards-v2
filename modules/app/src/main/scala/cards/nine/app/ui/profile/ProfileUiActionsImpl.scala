package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.app.Activity
import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.Tab
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TabLayoutTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.sharedcollections.SharedCollectionsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SnailsCommons, SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{CharDrawable, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.app.ui.profile.adapters.AccountOptions._
import com.fortysevendeg.ninecardslauncher.app.ui.profile.adapters.{AccountsAdapter, EmptyProfileAdapter, SubscriptionsAdapter}
import com.fortysevendeg.ninecardslauncher.app.ui.profile.dialog.{EditAccountDeviceDialogFragment, RemoveAccountDeviceDialogFragment}
import com.fortysevendeg.ninecardslauncher.app.ui.profile.models.AccountSync
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.commons.types.PublishedByOther
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{SharedCollection, Subscription}
import com.fortysevendeg.ninecardslauncher.process.theme.models.{CardLayoutBackgroundColor, PrimaryColor}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

trait ProfileUiActionsImpl
  extends ProfileUiActions
  with ProfileStyles
  with TabLayout.OnTabSelectedListener {

  self: TypedFindView with Contexts[AppCompatActivity] =>

  implicit val presenter: ProfilePresenter

  implicit val uiContext: UiContext[Activity]

  implicit lazy val theme = presenter.getTheme

  val tagDialog = "dialog"

  lazy val systemBarsTint = new SystemBarsTint

  lazy val rootLayout = findView(TR.profile_root)

  lazy val barLayout = findView(TR.profile_appbar)

  lazy val toolbar = findView(TR.profile_toolbar)

  lazy val userContainer = findView(TR.profile_user_container)

  lazy val userAvatar = findView(TR.profile_user_avatar)

  lazy val userName = findView(TR.profile_user_name)

  lazy val userEmail = findView(TR.profile_user_email)

  lazy val tabs = findView(TR.profile_tabs)

  lazy val recyclerView = findView(TR.profile_recycler)

  lazy val loadingView = findView(TR.profile_loading)

  lazy val iconIndicatorDrawable = PathMorphDrawable(
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  def showMessage(res: Int): Ui[Any] = rootLayout <~ vSnackbarShort(res)

  override def initialize(): Ui[Any] =
    (rootLayout <~ vBackgroundColor(theme.get(CardLayoutBackgroundColor))) ~
      (userContainer <~ vBackgroundColor(theme.get(PrimaryColor))) ~
      (barLayout <~ vBackgroundColor(theme.get(PrimaryColor))) ~
      (loadingView <~ sChangeProgressBarColor(theme.get(PrimaryColor))) ~
      (tabs <~ tlAddTabs(
        (resGetString(R.string.accounts), AccountsTab),
        (resGetString(R.string.publications), PublicationsTab),
        (resGetString(R.string.subscriptions), SubscriptionsTab))) ~
      (tabs <~ tlSetListener(this)) ~
      (recyclerView <~
        rvLayoutManager(new LinearLayoutManager(activityContextWrapper.application))) ~
      systemBarsTint.updateStatusColor(theme.get(PrimaryColor)) ~
      Ui(presenter.loadUserAccounts())

  override def showLoading(): Ui[Any] = (loadingView <~ vVisible) ~ (recyclerView <~ vInvisible)

  override def showAddCollectionMessage(sharedCollectionId: String): Ui[Any] = {
    val adapter = recyclerView.getAdapter match {
      case sharedCollectionsAdapter: SharedCollectionsAdapter =>
        val newCollections = sharedCollectionsAdapter.sharedCollections map {
          case col if col.sharedCollectionId == sharedCollectionId => col.copy(publicCollectionStatus = PublishedByOther)
          case col => col
        }
        sharedCollectionsAdapter.copy(sharedCollections = newCollections)
    }
    showMessage(R.string.collectionAdded) ~
      (recyclerView <~ rvSwapAdapter(adapter))
  }

  override def refreshCurrentSubscriptions(): Ui[Any] = // TODO Remove when we've got different states for the switch - issue #783
    recyclerView <~ rvSwapAdapter(recyclerView.getAdapter)

  override def showUpdatedSubscriptions(sharedCollectionId: String, subscribed: Boolean): Ui[Any] = {
    val adapter = recyclerView.getAdapter match {
      case subscriptionsAdapter: SubscriptionsAdapter =>
        val subscriptions = subscriptionsAdapter.subscriptions map {
          case subscription if subscription.sharedCollectionId == sharedCollectionId => subscription.copy(subscribed = subscribed)
          case subscription => subscription
        }
        subscriptionsAdapter.copy(subscriptions = subscriptions)
    }
    recyclerView <~ rvSwapAdapter(adapter)
  }

  override def showErrorSubscribing(triedToSubscribe: Boolean): Ui[Any] =
    showMessage(if (triedToSubscribe) R.string.errorSubscribing else R.string.errorUnsubscribing)

  override def showContactUsError(clickAction: () => Unit): Ui[Any] = showError(R.string.contactUsError, clickAction)

  override def showContactUsError(): Ui[Any] = uiShortToast2(R.string.contactUsError)

  override def showLoadingUserError(clickAction: () => Unit): Ui[Any] = showError(R.string.errorLoadingUser, clickAction)

  override def showSyncingError(): Ui[Any] = showMessage(R.string.errorSyncing) ~ (loadingView <~ vInvisible)

  override def showInvalidConfigurationNameError(action: () => Unit): Ui[Any] =
    rootLayout <~ vSnackbarIndefiniteAction(
      res = R.string.errorEmptyNameForDevice,
      buttonText = R.string.errorEmptyNameForDeviceButton,
      f = action)

  override def showErrorSavingCollectionInScreen(clickAction: () => Unit): Ui[Any] = showError(R.string.errorSavingPublicCollections, clickAction)

  override def showMessageAccountSynced(): Ui[Any] = showMessage(R.string.accountSynced) ~ (loadingView <~ vInvisible)

  override def userProfile(name: String, email: String, avatarUrl: Option[String]): Ui[Any] =
    (userName <~ tvText(name)) ~
    (userEmail <~ tvText(email)) ~
      (userAvatar <~
        (avatarUrl map ivUri getOrElse {
          val drawable = CharDrawable(name.substring(0, 1).toUpperCase)
          ivSrc(drawable)
        }) <~
        menuAvatarStyle)

  override def setAccountsAdapter(items: Seq[AccountSync]): Ui[Any] =
    (recyclerView <~ vVisible <~ rvAdapter(AccountsAdapter(items, accountClickListener))) ~
      (loadingView <~ vInvisible)

  override def setSubscriptionsAdapter(
    items: Seq[Subscription],
    onSubscribe: (String, Boolean) => Unit): Ui[Any] =
    (recyclerView <~ vVisible <~ rvAdapter(SubscriptionsAdapter(items, onSubscribe))) ~
      (loadingView <~ vInvisible)

  override def handleToolbarVisibility(percentage: Float): Ui[Any] = toolbar match {
    case t if percentage >= 0.5 && t.getVisibility == View.VISIBLE => toolbar <~ SnailsCommons.applyFadeOut()
    case t if percentage < 0.5 && t.getVisibility == View.INVISIBLE => toolbar <~ SnailsCommons.applyFadeIn()
    case _ => Ui.nop
  }

  override def handleProfileVisibility(percentage: Float): Ui[Any] = {
    val alpha = if (percentage <= 0.5f) 1f - (percentage * 2)  else 0f
    userContainer <~ vAlpha(alpha)
  }

  override def showDialogForDeleteDevice(cloudId: String): Unit =
    showDialog(new RemoveAccountDeviceDialogFragment(cloudId))

  override def showDialogForCopyDevice(cloudId: String, actualName: String): Unit =
    showDialog(new EditAccountDeviceDialogFragment(
      title = R.string.copyAccountSyncDialogTitle,
      maybeText = None,
      action = presenter.copyDevice(_, cloudId, actualName)))

  override def showDialogForRenameDevice(cloudId: String, actualName: String): Unit =
    showDialog(new EditAccountDeviceDialogFragment(
      title = R.string.renameAccountSyncDialogTitle,
      maybeText = Some(actualName),
      action = presenter.renameDevice(_, cloudId, actualName)))

  override def loadPublications(
    sharedCollections: Seq[SharedCollection],
    onAddCollection: (SharedCollection) => Unit,
    onShareCollection: (SharedCollection) => Unit): Ui[Any] = {
    val adapter = SharedCollectionsAdapter(sharedCollections, onAddCollection, onShareCollection)
    (recyclerView <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loadingView <~ vInvisible)
  }

  override def showEmptyPublicationsContent(error: Boolean, reload: () => Unit): Ui[Any] =
    showEmptyContent(PublicationsTab, error, reload)

  override def showEmptySubscriptionsContent(error: Boolean, reload: () => Unit): Ui[Any] =
    showEmptyContent(SubscriptionsTab, error, reload)

  override def showEmptyAccountsContent(error: Boolean, reload: () => Unit): Ui[Any] =
    showEmptyContent(AccountsTab, error, reload)

  private[this] def showEmptyContent(tab: ProfileTab, error: Boolean, reload: () => Unit): Ui[Any] =
    (recyclerView <~
      vVisible <~
      rvAdapter(EmptyProfileAdapter(tab, error, reload))) ~
      (loadingView <~ vInvisible)

  private[this] def showDialog(dialog: DialogFragment): Unit = {
    activityContextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        dialog.show(ft, tagDialog)
      case _ =>
    }
  }

  private[this] def showError(message: Int, clickAction: () => Unit): Ui[Any] =
    (rootLayout <~ vSnackbarIndefiniteAction(message, R.string.buttonErrorReload, clickAction)) ~
      (loadingView <~ vInvisible)

  private[this] def accountClickListener(accountOption: AccountOption, accountSync: AccountSync): Unit =
    (accountOption, accountSync.cloudId) match {
      case (SyncOption, _) => presenter.launchService()
      case (DeleteOption, Some(cloudId)) => showDialogForDeleteDevice(cloudId)
      case (CopyOption, Some(cloudId)) => showDialogForCopyDevice(cloudId, accountSync.title)
      case (ChangeNameOption, Some(cloudId)) => showDialogForRenameDevice(cloudId, accountSync.title)
      case (PrintInfoOption, Some(cloudId)) => presenter.printDeviceInfo(cloudId)
      case _ =>
    }

  override def onTabReselected(tab: Tab): Unit = {}

  override def onTabUnselected(tab: Tab): Unit = {}

  override def onTabSelected(tab: Tab): Unit = tab.getTag match {
    case AccountsTab => presenter.loadUserAccounts()
    case PublicationsTab => presenter.loadPublications()
    case SubscriptionsTab => presenter.loadSubscriptions()
    case _ =>
  }

}
