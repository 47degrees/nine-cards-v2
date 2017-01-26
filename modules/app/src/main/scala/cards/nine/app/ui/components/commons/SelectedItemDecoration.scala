/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.components.commons

import android.graphics.Canvas
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.{ItemDecoration, State}
import android.view.View
import cards.nine.app.ui.commons.ops.ViewOps._
import cards.nine.app.ui.components.layouts.FastScrollerView
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.{CardTextColor, SearchBackgroundColor}
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

class SelectedItemDecoration(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme)
    extends ItemDecoration {

  val size = resGetDimensionPixelSize(R.dimen.padding_xlarge)

  val stroke = resGetDimensionPixelSize(R.dimen.stroke_thin)

  val line = {
    val d = new ShapeDrawable(new RectShape)
    d.getPaint.setColor(theme.get(CardTextColor))
    d
  }

  val divider = {
    val d = new ShapeDrawable(new RectShape)
    d.getPaint.setColor(theme.get(SearchBackgroundColor).alpha(.6f))
    d
  }

  override def onDrawOver(c: Canvas, parent: RecyclerView, state: State): Unit = {
    super.onDraw(c, parent, state)
    for {
      recyclerView <- Option(parent)
      pos          <- recyclerView.getField[Int](FastScrollerView.fastScrollerPositionKey)
      count        <- recyclerView.getField[Int](FastScrollerView.fastScrollerCountKey)
    } yield {
      val showLine = recyclerView.getField[Boolean](SelectedItemDecoration.showLine) getOrElse false
      (0 to recyclerView.getChildCount flatMap (i => Option(recyclerView.getChildAt(i)))) foreach {
        view =>
          val viewPosition = parent.getChildAdapterPosition(view)
          draw(c, view, viewPosition, pos, count, showLine)
      }
    }
  }

  private[this] def draw(
      c: Canvas,
      child: View,
      viewPosition: Int,
      pos: Int,
      count: Int,
      showLine: Boolean) = {
    if (viewPosition < pos || viewPosition >= (pos + count)) {
      divider.setBounds(child.getLeft, child.getTop, child.getRight, child.getBottom)
      divider.draw(c)
    } else if (showLine) {
      val left   = child.getLeft + (child.getWidth / 2) - (size / 2)
      val right  = left + size
      val top    = child.getTop + child.getHeight - child.getPaddingBottom
      val bottom = top + stroke
      line.setBounds(left, top, right, bottom)
      line.draw(c)
    }
  }

}

object SelectedItemDecoration {
  val showLine = "show_line"
}
