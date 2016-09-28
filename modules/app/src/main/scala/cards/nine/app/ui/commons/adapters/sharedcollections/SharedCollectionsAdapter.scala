package cards.nine.app.ui.commons.adapters.sharedcollections

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, ViewGroup}
import cards.nine.app.ui.commons.UiContext
import cards.nine.process.sharedcollections.models.SharedCollection
import cards.nine.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.TR
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import macroid.ActivityContextWrapper

case class SharedCollectionsAdapter(
  sharedCollections: Seq[SharedCollection],
  onAddCollection: (SharedCollection) => Unit,
  onShareCollection: (SharedCollection) => Unit)
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], theme: NineCardsTheme)
  extends RecyclerView.Adapter[ViewHolderSharedCollectionsLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSharedCollectionsLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.public_collections_item, parent, false)
    ViewHolderSharedCollectionsLayoutAdapter(view)
  }

  override def getItemCount: Int = sharedCollections.size

  override def onBindViewHolder(viewHolder: ViewHolderSharedCollectionsLayoutAdapter, position: Int): Unit = {
    val publicCollection = sharedCollections(position)
    viewHolder.bind(publicCollection, onAddCollection(publicCollection), onShareCollection(publicCollection)).run
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}
