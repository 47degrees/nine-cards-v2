package com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders

import android.content.{ClipData, Context}
import android.graphics.Point
import android.os.Handler
import android.view.DragEvent._
import android.view.{LayoutInflater, View}
import android.widget._
import com.fortysevendeg.macroid.extras.GridLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.CommonsTweak._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.CollectionOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{DragObject, PositionsUtils}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.DropBackgroundDrawable
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.LauncherWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.{Dimen, LauncherWorkSpaceHolder, LauncherWorkSpaces}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drag.CollectionShadowBuilder
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders.LauncherWorkSpaceCollectionsHolder.positionDraggingItem
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.types.ReorderCollection
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.{FontSize, IconsSize, SpeedAnimations}
import com.fortysevendeg.ninecardslauncher.commons.ops.SeqOps._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid.FullDsl._
import macroid._

class LauncherWorkSpaceCollectionsHolder(context: Context, presenter: LauncherPresenter, parentDimen: Dimen)
  extends LauncherWorkSpaceHolder(context)
  with Contexts[View]
  with TypedFindView {

  LayoutInflater.from(context).inflate(R.layout.collections_workspace_layout, this)

  val selectedScale = 1.1f

  val defaultScale = 1f

  val handler = new Handler()

  var task: Option[Runnable] = None

  var moveTask: Option[Runnable] = None

  val sizeEdgeBetweenWorkspaces = resGetDimensionPixelSize(R.dimen.size_edge_between_workspaces)

  val widthSpace = parentDimen.width / numInLine

  val heightSpace = parentDimen.height / numInLine

  var positionScreen = 0

  val grid = Option(findView(TR.launcher_collections_grid))

  val views: Seq[CollectionItem] = 0 until numSpaces map (_ => new CollectionItem(context))

  (grid <~
    glAddViews(
      views = views,
      columns = numInLine,
      rows = numInLine,
      width = widthSpace,
      height = heightSpace)).run

  def populate(collections: Seq[Collection], positionScreen: Int): Ui[_] = {
    this.positionScreen = positionScreen
    val uiSeq = for {
      row <- 0 until numInLine
      column <- 0 until numInLine
    } yield {
      val position = (row * numInLine) + column
      val view = grid flatMap (_.getChildAt(position) match {
        case item: CollectionItem => Some(item)
        case _ => None
      })
      collections.lift(position) map { collection =>
        view <~ ciPopulate(collection)
      } getOrElse view <~ ciOff()
    }
    Ui.sequence(uiSeq: _*)
  }

  def prepareItemsScreenInReorder(position: Int): Ui[Any] = {
    val startReorder = presenter.statuses.startPositionReorderMode
    val screenOfCollection = (toPositionCollection(0) <= startReorder) && (toPositionCollection(numSpaces) > startReorder)
    def isConvertToDragging(pos: Int) = if (screenOfCollection) {
      pos == startReorder
    } else {
      pos == toPositionCollection(position)
    }
    Ui.sequence(views map { view =>
      if (isConvertToDragging(view.positionInGrid)) {
        view.convertToDraggingItem() ~ (view <~ vInvisible)
      } else if (view.collection.isEmpty) {
        view <~ vInvisible
      } else {
        view <~ vVisible
      }
    }: _*) ~ reorder(startReorder, position, animation = false)
  }

  def dragAddItemController(action: Int, x: Float, y: Float): Unit = {
    action match {
      case ACTION_DRAG_LOCATION =>
        val lastCurrentPosition = presenter.statuses.currentDraggingPosition
        val (canMoveToLeft, canMoveToRight) = canMove
        (calculateEdge(x), canMoveToLeft, canMoveToRight) match {
          case (LeftEdge, true, _) =>
            unselectAll().run
            delayedTask(() => {
              presenter.draggingAddItemToPreviousScreen(toPositionCollection(numSpaces - 1) - numSpaces)
            })
          case (RightEdge, _, true) =>
            unselectAll().run
            delayedTask(() => {
              presenter.draggingAddItemToNextScreen(toPositionCollection(0) + numSpaces)
            })
          case (NoEdge, _, _) =>
            clearTask()
            val space = calculatePosition(x, y)
            val currentPosition = toPositionCollection(space)
            if (lastCurrentPosition != currentPosition) {
              select(currentPosition).run
              presenter.draggingAddItemTo(currentPosition)
            }
          case _ =>
        }
      case ACTION_DROP =>
        unselectAll().run
        presenter.endAddItemToCollection()
      case ACTION_DRAG_EXITED =>
        unselectAll().run
      case ACTION_DRAG_ENDED =>
        unselectAll().run
        presenter.endAddItem()
      case _ =>
    }
  }

  def dragReorderCollectionController(action: Int, x: Float, y: Float): Unit = {
    (action, presenter.statuses.isReordering, isRunningReorderAnimation) match {
      case (ACTION_DRAG_LOCATION, true, false) =>
        val lastCurrentPosition = presenter.statuses.currentDraggingPosition
        val (canMoveToLeft, canMoveToRight) = canMove
        (calculateEdge(x), canMoveToLeft, canMoveToRight) match {
          case (LeftEdge, true, _) =>
            clearMoveTask()
            delayedTask(() => {
              resetAllPositions().run
              presenter.draggingReorderToPreviousScreen(toPositionCollection(numSpaces - 1) - numSpaces)
            })
          case (RightEdge, _, true) =>
            clearMoveTask()
            delayedTask(() => {
              resetAllPositions().run
              presenter.draggingReorderToNextScreen(toPositionCollection(0) + numSpaces)
            })
          case (NoEdge, _, _) =>
            clearTask()
            val space = calculatePosition(x, y)
            val existCollectionInSpace = (views.lift(space) flatMap (_.collection)).isDefined
            val currentPosition = toPositionCollection(space)
            if (existCollectionInSpace && lastCurrentPosition != currentPosition) {
              delayedMoveTask(() => {
                reorder(lastCurrentPosition, currentPosition).run
                presenter.draggingReorderTo(currentPosition)
              })
            }
          case _ => clearTask()
        }
      case (ACTION_DROP | ACTION_DRAG_ENDED, true, false) =>
        dragEnded()
      case (ACTION_DROP | ACTION_DRAG_ENDED, true, true) =>
        // we are waiting that the animation is finished in order to reset views
        delayedTask(dragEnded, SpeedAnimations.getDuration)
      case (ACTION_DRAG_EXITED, _, _) => clearMoveTask()
      case _ =>
    }
  }

  private[this] def canMove: (Boolean, Boolean) = getParent.getParent match {
    case workspaces: LauncherWorkSpaces =>
      ((workspaces ~> lwsCanMoveToPreviousScreenOnlyCollections()).get,
        (workspaces ~> lwsCanMoveToNextScreenOnlyCollections()).get)
    case _ => (false, false)
  }

  private[this] def dragEnded(): Unit = {
    clearTask()
    resetPlaces.run
    presenter.dropReorder()
  }

  private[this] def resetAllPositions(): Ui[Any] = Ui.sequence(views map { view =>
    view <~ backToPosition() <~ (view.collection map (_ => vVisible) getOrElse vInvisible)
  }: _*)

  private[this] def select(position: Int): Ui[Any] = Ui.sequence(views map { view =>
    view <~ (if (view.positionInGrid == position) ciDroppingOn() else ciDroppingOff())
  }: _*)

  private[this] def unselectAll(): Ui[Any] = select(Int.MaxValue)

  private[this] def reorder(currentPosition: Int, toPosition: Int, animation: Boolean = true): Ui[Any] =
    if (currentPosition < toPosition) {
      val from = currentPosition + 1
      val to = toPosition
      val transforms = from to to map { pos =>
        move(pos, pos - 1, animation)
      }
      val updatePositions = from to to map { pos =>
        getView(pos) map (view => Ui(view.positionInGrid = pos - 1)) getOrElse Ui.nop
      }
      Ui.sequence(transforms ++ updatePositions: _*)
    } else if (currentPosition > toPosition) {
      val from = toPosition
      val to = currentPosition
      val transforms = from until to map { pos =>
        move(pos, pos + 1, animation)
      }
      val updatePositions = from until to map { pos =>
        getView(pos) map (view => Ui(view.positionInGrid = pos + 1)) getOrElse Ui.nop
      }
      Ui.sequence(transforms ++ updatePositions: _*)
    } else {
      Ui.nop
    }

  private[this] def move(from: Int, to: Int, animation: Boolean): Ui[Any] = {
    val (fromColumn, fromRow) = place(from)
    val (toColumn, toRow) = place(to)
    val displacementHorizontal = (toColumn - fromColumn) * widthSpace
    val displacementVertical = (toRow - fromRow) * heightSpace
    val view = getView(from)
    if (animation) {
      view <~ applyAnimation(
        xBy = Some(displacementHorizontal),
        yBy = Some(displacementVertical))
    } else {
      view <~
        vTranslationX(displacementHorizontal) <~
        vTranslationY(displacementVertical)
    }
  }

  private[this] def resetPlaces: Ui[Any] = {
    val start = toPositionGrid(presenter.statuses.startPositionReorderMode)
    val current = toPositionGrid(presenter.statuses.currentDraggingPosition)
    val collectionsReordered = (views map (_.collection)).reorder(start, current).zipWithIndex map {
      case (collection, index) =>
        val positionCollection = toPositionCollection(index)
        // if it's the collection that the user is dragging, we put the collection stored.
        // when the user is reordering in other screen the collection isn't the same on the view
        if (positionCollection == presenter.statuses.currentDraggingPosition) {
          presenter.statuses.collectionReorderMode
        } else {
          collection map (_.copy(position = positionCollection))
        }
    }
    Ui.sequence(views.zip(collectionsReordered) map {
      case (view, Some(collection)) =>
        view <~
          vClearAnimation <~
          backToPosition() <~
          ciPopulate(collection)
      case _ => Ui.nop
    }: _*)
  }

  private[this] def place(pos: Int): (Int, Int) = {
    val row = pos / numInLine
    val column = pos % numInLine
    (column, row)
  }

  private[this] def getView(position: Int): Option[CollectionItem] = views.find(_.positionInGrid == position)

  private[this] def calculatePosition(x: Float, y: Float): Int = {
    val column = x.toInt / widthSpace
    val row = y.toInt / heightSpace
    (row * numInLine) + column
  }

  private[this] def calculateEdge(x: Float): Edge = if (x < sizeEdgeBetweenWorkspaces) {
    LeftEdge
  } else if (x > parentDimen.width - sizeEdgeBetweenWorkspaces) {
    RightEdge
  } else {
    NoEdge
  }

  private[this] def toPositionCollection(position: Int) = position + (positionScreen * numSpaces)

  private[this] def toPositionGrid(position: Int) = position - (positionScreen * numSpaces)

  private[this] def isRunningReorderAnimation: Boolean = views exists (_.isRunningAnimation)

  private[this] def delayedMoveTask(runTask: () => Unit, duration: Int = 200): Unit = {
    moveTask foreach handler.removeCallbacks
    val runnable = new Runnable {
      override def run(): Unit = runTask()
    }
    moveTask = Option(runnable)
    handler.postDelayed(runnable, duration)
  }

  private[this] def clearMoveTask(): Unit = if (moveTask.isDefined) {
    moveTask foreach handler.removeCallbacks
    moveTask = None
  }

  private[this] def delayedTask(runTask: () => Unit, duration: Int = 500): Unit = if (task.isEmpty) {
    val runnable = new Runnable {
      override def run(): Unit = runTask()
    }
    task = Option(runnable)
    handler.postDelayed(runnable, duration)
  }

  private[this] def clearTask(): Unit = if (task.isDefined) {
    task foreach handler.removeCallbacks
    task = None
  }

  private[this] def backToPosition() = vTranslationX(0) + vTranslationY(0)

  private[this] def ciPopulate(collection: Collection) = vVisible + Tweak[CollectionItem](_.populate(collection))

  private[this] def ciOff() = vInvisible + Tweak[CollectionItem](_.collection = None)

  private[this] def ciDroppingOn() = Tweak[CollectionItem](_.droppingOn().run)

  private[this] def ciDroppingOff() = Tweak[CollectionItem](_.droppingOff().run)

  class CollectionItem(context: Context)
    extends FrameLayout(context)
    with TypedFindView { self =>

    LayoutInflater.from(getContext).inflate(TR.layout.collection_item, self, true)

    var positionInGrid = 0

    var collection: Option[Collection] = None

    val displacement = resGetDimensionPixelSize(R.dimen.shadow_displacement_default)

    val radius = resGetDimensionPixelSize(R.dimen.shadow_radius_default)

    lazy val layout = Option(findView(TR.launcher_collection_item_layout))

    lazy val iconRoot = Option(findView(TR.launcher_collection_item_icon_root))

    lazy val icon = Option(findView(TR.launcher_collection_item_icon))

    lazy val name = Option(findView(TR.launcher_collection_item_name))

    val dropBackgroundIcon = new DropBackgroundDrawable

    ((layout <~ vUseLayerHardware) ~
      (name <~ tvShadowLayer(radius, displacement, displacement, resGetColor(R.color.shadow_default)))).run

    def populate(collection: Collection): Unit = {
      this.collection = Some(collection)
      positionInGrid = collection.position
      val resIcon = collection.getIconWorkspace
      ((layout <~
        FuncOn.click { view: View =>
          val (x, y) = PositionsUtils.calculateAnchorViewPosition(view)
          val point = new Point(x + (view.getWidth / 2), y + (view.getHeight / 2))
          Ui(presenter.goToCollection(this.collection, point))
        } <~
        On.longClick {
          presenter.startReorder(this.collection, positionInGrid)
          (this.collection map { _ =>
            (this <~ vInvisible) ~ convertToDraggingItem() ~ (layout <~ startDragStyle(collection.id.toString, collection.name))
          } getOrElse Ui.nop) ~ Ui(true)
        }) ~
        (icon <~ vResize(IconsSize.getIconCollection) <~ ivSrc(resIcon) <~ vBackgroundCollection(collection.themedColorIndex)) ~
        (name <~ tvSizeResource(FontSize.getSizeResource) <~ tvText(collection.name))).run
    }

    def convertToDraggingItem(): Ui[Any] = Ui(positionInGrid = positionDraggingItem)

    def droppingOn(): Ui[Any] =
      (iconRoot <~ vBackground(dropBackgroundIcon)) ~
        dropBackgroundIcon.start() ~
        (name <~ vInvisible)

    def droppingOff(): Ui[Any] =
      dropBackgroundIcon.end() ~~
        (iconRoot <~ vBlankBackground) ~
        (name <~ vVisible)

    def startDragStyle(label: String, description: String): Tweak[View] = Tweak[View] { view =>
      val dragData = ClipData.newPlainText(label, description)
      val shadow = new CollectionShadowBuilder(view)
      view.startDrag(dragData, shadow, DragObject(shadow, ReorderCollection), 0)
    }

  }

}

object LauncherWorkSpaceCollectionsHolder {
  val positionDraggingItem = Int.MaxValue
}

sealed trait Edge

case object LeftEdge extends Edge

case object RightEdge extends Edge

case object NoEdge extends Edge