package cards.nine.app.ui.commons.actions

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import cards.nine.app.ui.components.drawables.{IconTypes, PathMorphDrawable}
import cards.nine.app.ui.components.layouts.tweaks.FastScrollerLayoutTweak._
import cards.nine.commons.ops.ColorOps._
import cards.nine.models.NineCardsTheme
import cards.nine.models.types.theme.DrawerTabsBackgroundColor
import com.fortysevendeg.macroid.extras.FloatingActionButtonTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ContextWrapper, Tweak}

trait Styles {

  def recyclerStyle(implicit context: ContextWrapper): Tweak[RecyclerView] = rvFixedSize

  def fabButtonMenuStyle(color: Int)(implicit context: ContextWrapper): Tweak[FloatingActionButton] = {
    val iconFabButton = PathMorphDrawable(
      defaultIcon = IconTypes.CHECK,
      defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default))
    val darkColor = color.dark()
    ivSrc(iconFabButton) +
      fbaColor(color, darkColor)
  }

  def scrollableStyle(color: Int)(implicit context: ContextWrapper, theme: NineCardsTheme) =
    fslColor(color, theme.get(DrawerTabsBackgroundColor))

}

