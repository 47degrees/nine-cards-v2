package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup.LayoutParams._
import android.view.{View, ViewGroup}
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.styles.{CommonStyles, CollectionCardsStyles}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.PrivateCollectionOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Collection, PrivateCard, PrivateCollection}
import com.fortysevendeg.ninecardslauncher.process.theme.models.{CardLayoutBackgroundColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import com.google.android.flexbox.FlexboxLayout
import macroid.FullDsl._
import macroid._

trait PrivateCollectionsActionsImpl
  extends PrivateCollectionsActions
  with Styles
  with NineCardIntentConversions {

  self: TypedFindView with BaseActionFragment with Contexts[Fragment] =>

  val launcherPresenter: LauncherPresenter

  implicit val collectionPresenter: PrivateCollectionsPresenter

  lazy val recycler = Option(findView(TR.actions_recycler))

  def loadBackgroundColor = theme.get(CardLayoutBackgroundColor)

  override def initialize(): Ui[Any] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.myCollections) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle)

  override def addPrivateCollections(privateCollections: Seq[PrivateCollection]): Ui[Any] = {
    val adapter = PrivateCollectionsAdapter(privateCollections)
    (recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)
  }

  override def addCollection(collection: Collection): Ui[Any] = Ui {
    launcherPresenter.addCollection(collection)
  }

  override def showLoading(): Ui[Any] = (loading <~ vVisible) ~ (recycler <~ vGone)

  override def showEmptyMessageInScreen(): Ui[Any] =
    showMessageInScreen(R.string.emptyPrivateCollections, error = false, collectionPresenter.loadPrivateCollections())

  override def showErrorLoadingCollectionInScreen(): Ui[Any] =
    showMessageInScreen(R.string.errorLoadingPrivateCollections, error = true, collectionPresenter.loadPrivateCollections())

  override def showErrorSavingCollectionInScreen(): Ui[Any] =
    showMessageInScreen(R.string.errorSavingPrivateCollections, error = true, collectionPresenter.loadPrivateCollections())

  override def close(): Ui[Any] = unreveal()

  private[this] def showMessage(message: Int): Ui[Any] = content <~ vSnackbarShort(message)

}

case class ViewHolderPrivateCollectionsLayoutAdapter(
  content: ViewGroup)(implicit context: ActivityContextWrapper, uiContext: UiContext[_], presenter: PrivateCollectionsPresenter, val theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(content)
  with TypedFindView
  with CollectionCardsStyles
  with CommonStyles {

  val appsByRow = 5

  lazy val root = findView(TR.private_collections_item_layout)

  lazy val iconContent = findView(TR.private_collections_item_content)

  lazy val icon = findView(TR.private_collections_item_icon)

  lazy val name = findView(TR.private_collections_item_name)

  lazy val appsRow = findView(TR.private_collections_item_row)

  lazy val addCollection = findView(TR.private_collections_item_add_collection)

  ((root <~ cardRootStyle) ~
    (name <~ titleTextStyle) ~
    (addCollection <~ buttonStyle)).run

  def bind(privateCollection: PrivateCollection, position: Int): Ui[_] = {
    val d = new ShapeDrawable(new OvalShape)
    d.getPaint.setColor(resGetColor(getIndexColor(privateCollection.themedColorIndex)))
    val cardsRow = privateCollection.cards
    (iconContent <~ vBackground(d)) ~
      (icon <~ ivSrc(privateCollection.getIconCollectionDetail)) ~
      (appsRow <~
        vgRemoveAllViews <~
        automaticAlignment(appsRow, cardsRow)) ~
      (name <~ tvText(privateCollection.name)) ~
      (content <~ vTag(position)) ~
      (addCollection <~ On.click(Ui(presenter.saveCollection(privateCollection))))
  }

  override def findViewById(id: Int): View = content.findViewById(id)

  private[this] def automaticAlignment(view: FlexboxLayout, cards: Seq[PrivateCard]): Tweak[FlexboxLayout] = {
    val width = view.getWidth
    if (width > 0) {
      vgAddViews(getViewsByCards(cards, width))
    } else {
      vGlobalLayoutListener { v => {
        appsRow <~ vgAddViews(getViewsByCards(cards, v.getWidth))
      }}
    }
  }

  private[this] def getViewsByCards(cards: Seq[PrivateCard], width: Int) = {
    val sizeIcon = resGetDimensionPixelSize(R.dimen.size_icon_item_collections_content)
    val sizeView = width / appsByRow
    val padding = (sizeView - sizeIcon) / 2
    cards.zipWithIndex  map {
      case (card, index) =>
        (w[ImageView] <~
          lp[FlexboxLayout](sizeView, WRAP_CONTENT) <~
          vPadding(padding, 0, padding, 0) <~
          ivSrcByPackageName(card.packageName, card.term)).get
    }
  }
}