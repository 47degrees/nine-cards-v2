package cards.nine.app.ui.components.widgets

import android.appwidget.AppWidgetHostView
import android.view.MotionEvent._
import android.view.View.OnTouchListener
import android.view.{GestureDetector, MotionEvent, View}
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.commons.ops.WidgetsOps.Cell
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.launcher.EditWidgetsMode
import cards.nine.commons._
import cards.nine.models.Widget
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.jobs.WidgetsJobs

case class LauncherWidgetView(id: Int, widgetView: AppWidgetHostView)(implicit contextWrapper: ContextWrapper, widgetJobs: WidgetsJobs)
  extends FrameLayout(contextWrapper.bestAvailable) {

  val paddingDefault = resGetDimensionPixelSize(R.dimen.padding_default)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin)

  var launcherWidgetViewStatuses = LauncherWidgetViewStatuses()

  val gestureDetector = new GestureDetector(getContext, new GestureDetector.SimpleOnGestureListener() {
    override def onLongPress(e: MotionEvent): Unit = if (launcherWidgetViewStatuses.canEdit) widgetJobs.openModeEditWidgets(id).resolveAsync()
  })

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = gestureDetector.onTouchEvent(event)

  override def onTouchEvent(event: MotionEvent): Boolean = {
    event.getAction match {
      case ACTION_DOWN => launcherWidgetViewStatuses = launcherWidgetViewStatuses.copy(canEdit = true)
      case ACTION_UP | ACTION_CANCEL => launcherWidgetViewStatuses =  launcherWidgetViewStatuses.copy(canEdit = false)
      case _ =>
    }
    true
  }

  val viewBlockTouch = w[FrameLayout].get
  viewBlockTouch.setOnTouchListener(new OnTouchListener {
    override def onTouch(v: View, event: MotionEvent): Boolean = {
      event.getAction match {
        case ACTION_DOWN =>
          statuses = statuses.copy(touchingWidget = true)
          if (statuses.mode == EditWidgetsMode) widgetJobs.loadViewEditWidgets(id).resolveAsync()
        case _ =>
      }
      false
    }
  })

  (this <~ vgAddViews(Seq(widgetView, viewBlockTouch))).run

  def activeSelected(): Ui[Any] = this <~ vBackground(R.drawable.stroke_widget_selected)

  def activeResizing(): Ui[Any] = this <~ vBackground(R.drawable.stroke_widget_resizing)

  def activeMoving(): Ui[Any] = this <~ vBackground(R.drawable.stroke_widget_moving)

  def deactivateSelected(): Ui[Any] = this <~ vBlankBackground

  def adaptSize(widget: Widget): Ui[Any] = this.getField[Cell](LauncherWidgetView.cellKey) match {
    case Some(cell) => Ui {
      updateWidgetSize(cell, widget)
      setLayoutParams(createParams(cell, widget))
    }
    case _ => Ui.nop
  }

  def addView(cell: Cell, widget: Widget): Tweak[FrameLayout] = {
    updateWidgetSize(cell, widget)
    vgAddView(this, createParams(cell, widget))
  }

  private[this] def createParams(cell: Cell, widget: Widget): LayoutParams = {
    val (width, height) = cell.getSize(widget.area.spanX, widget.area.spanY)
    val (startX, startY) = cell.getSize(widget.area.startX, widget.area.startY)
    val params = new LayoutParams(width  + stroke, height + stroke)
    val left = paddingDefault + startX
    val top = paddingDefault + startY
    params.setMargins(left, top, paddingDefault, paddingDefault)
    params
  }

  private[this] def updateWidgetSize(cell: Cell, widget: Widget): Unit = {
    val density: Float = getResources.getDisplayMetrics.density
    val (width, height) = cell.getSize(widget.area.spanX, widget.area.spanY) match {
      case (w, h) => (((w - paddingDefault) / density).toInt, ((h - paddingDefault) / density).toInt)
    }
    widgetView.updateAppWidgetSize(javaNull, width, height, width, height)
    widgetView.requestLayout()
  }

}

case class LauncherWidgetViewStatuses(canEdit: Boolean = true)

object LauncherWidgetView {
  val cellKey = "cell"
  val widgetKey = "widget"
}
