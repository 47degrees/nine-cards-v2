package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionFragment._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{FragmentUiContext, UiContext, UiExtensions}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import macroid._
import rapture.core.Answer

class CollectionFragment
  extends Fragment
  with Contexts[Fragment]
  with ContextSupportProvider
  with UiExtensions
  with CollectionFragmentComposer {

  lazy val di = new Injector

  implicit lazy val theme: NineCardsTheme = di.themeProcess.getSelectedTheme.run.run match {
    case Answer(t) => t
    case _ => getDefaultTheme
  }

  implicit lazy val uiContext: UiContext[Fragment] = FragmentUiContext(this)

  lazy val animateCards = getBoolean(Seq(getArguments), keyAnimateCards, default = false)

  lazy val position = getInt(Seq(getArguments), keyPosition, 0)

  lazy val collection = Option(getSerialize[Collection](Seq(getArguments), keyCollection, javaNull))

  lazy val collectionId = getInt(Seq(getArguments), keyCollectionId, 0)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View =
    layout(animateCards)

  override def onViewCreated(view: View, savedInstanceState: Bundle): Unit = {
    sType = getArguments.getInt(keyScrollType, ScrollType.down)
    canScroll = collection exists (_.cards.length > numSpaces)
    collection foreach (c => initUi(c, animateCards).run)
    super.onViewCreated(view, savedInstanceState)
  }

  override def onAttach(activity: Activity): Unit = {
    super.onAttach(activity)
    activity match {
      case scroll: ScrolledListener => scrolledListener = Some(scroll)
      case _ =>
    }
  }

  override def onDetach(): Unit = {
    super.onDetach()
    scrolledListener = None
  }

  def bindAnimatedAdapter = if (animateCards) collection foreach (c => setAnimatedAdapter(c).run)

  def addCards(cards: Seq[Card]) = getAdapter foreach { adapter =>
    adapter.addCards(cards)
    val cardCount = adapter.collection.cards.length
    canScroll = cardCount > numSpaces
    resetScroll(adapter.collection).run
  }

  def removeCard(card: Card) = getAdapter foreach { adapter =>
    adapter.removeCard(card)
    val cardCount = adapter.collection.cards.length
    canScroll = cardCount > numSpaces
    resetScroll(adapter.collection).run
  }

  def reloadCards(cards: Seq[Card]) = getAdapter foreach { adapter =>
    adapter.updateCards(cards)
    val cardCount = adapter.collection.cards.length
    canScroll = cardCount > numSpaces
    resetScroll(adapter.collection).run
  }
}

object CollectionFragment {
  val keyPosition = "tab_position"
  val keyCollection = "collection"
  val keyCollectionId = "collection_id"
  val keyScrollType = "scroll_type"
  val keyAnimateCards = "animate_cards"
}

