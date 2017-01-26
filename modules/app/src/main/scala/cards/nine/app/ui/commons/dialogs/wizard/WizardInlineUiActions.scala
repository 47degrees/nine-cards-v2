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

package cards.nine.app.ui.commons.dialogs.wizard

import android.support.v4.app.{Fragment, FragmentManager}
import android.view.ViewGroup
import android.widget.ImageView
import cards.nine.app.ui.commons.SnailsCommons._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.CommonsExcerpt._
import cards.nine.app.ui.components.layouts.WizardInlineData
import cards.nine.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import cards.nine.app.ui.components.layouts.tweaks.WizardInlineWorkspacesTweaks._
import cards.nine.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._
import macroid.extras.ImageViewTweaks._
import macroid.extras.LinearLayoutTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewGroupTweaks._
import macroid.extras.ViewTweaks._

class WizardInlineUiActions(dom: WizardInlineDOM, listener: WizardListener)(
    implicit activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  def initialize(wizardInlineType: WizardInlineType): TaskService[Unit] = {

    val steps = getSteps(wizardInlineType)

    def pagination(position: Int) =
      (w[ImageView] <~ paginationItemStyle <~ ivSrc(R.drawable.wizard_inline_pager) <~ vTag(
        position.toString)).get

    def createPagers() = {
      val pagerViews = steps.indices map { position =>
        val view = pagination(position)
        view.setActivated(position == 0)
        view
      }
      dom.wizardInlinePagination <~ vgAddViews(pagerViews)
    }

    def reloadPagers(currentPage: Int) = Transformer {
      case i: ImageView if Option(i.getTag).isDefined && i.getTag.equals(currentPage.toString) =>
        i <~ vActivated(true)
      case i: ImageView => i <~ vActivated(false)
    }

    ((dom.wizardInlineWorkspace <~
      vGlobalLayoutListener(_ => {
        dom.wizardInlineWorkspace <~
          wiwData(steps) <~
          awsAddPageChangedObserver(currentPage => {
            val showAction = currentPage == steps.length - 1
            ((dom.wizardInlinePagination <~ reloadPagers(currentPage)) ~
              ((showAction,
                (dom.wizardInlineGotIt ~> isVisible).get,
                (dom.wizardInlinePagination ~> isVisible).get) match {
                case (true, false, _) =>
                  (dom.wizardInlineGotIt <~ applyFadeIn()) ~
                    (dom.wizardInlineSkip <~ applyFadeOut()) ~
                    (dom.wizardInlinePagination <~ applyFadeOut())
                case (false, _, false) =>
                  (dom.wizardInlineGotIt <~ applyFadeOut()) ~
                    (dom.wizardInlineSkip <~ applyFadeIn()) ~
                    (dom.wizardInlinePagination <~ applyFadeIn())
                case _ => Ui.nop
              })).run
          })
      })) ~
      (dom.wizardInlineSkip <~
        On.click(Ui(listener.dismissWizard()))) ~
      (dom.wizardInlineGotIt <~
        vGone <~
        On.click(Ui(listener.dismissWizard()))) ~
      createPagers()).toService()
  }

  private[this] def getSteps(wizardInlineType: WizardInlineType) =
    wizardInlineType match {
      case AppDrawerWizardInline =>
        Seq(
          WizardInlineData(
            R.drawable.wizard_inline_appdrawer_01,
            resGetString(R.string.wizard_inline_appdrawer_title_1),
            resGetString(R.string.wizard_inline_appdrawer_1)),
          WizardInlineData(
            R.drawable.wizard_inline_appdrawer_02,
            resGetString(R.string.wizard_inline_appdrawer_title_2),
            resGetString(R.string.wizard_inline_appdrawer_2)),
          WizardInlineData(
            R.drawable.wizard_inline_appdrawer_03,
            resGetString(R.string.wizard_inline_appdrawer_title_3),
            resGetString(R.string.wizard_inline_appdrawer_3)))
      case LauncherWizardInline =>
        Seq(
          WizardInlineData(
            R.drawable.wizard_inline_launcher_01,
            resGetString(R.string.wizard_inline_launcher_title_1),
            resGetString(R.string.wizard_inline_launcher_1)),
          WizardInlineData(
            R.drawable.wizard_inline_launcher_02,
            resGetString(R.string.wizard_inline_launcher_title_2),
            resGetString(R.string.wizard_inline_launcher_2)),
          WizardInlineData(
            R.drawable.wizard_inline_launcher_03,
            resGetString(R.string.wizard_inline_launcher_title_3),
            resGetString(R.string.wizard_inline_launcher_3)),
          WizardInlineData(
            R.drawable.wizard_inline_launcher_04,
            resGetString(R.string.wizard_inline_launcher_title_4),
            resGetString(R.string.wizard_inline_launcher_4)))
      case CollectionsWizardInline =>
        Seq(
          WizardInlineData(
            R.drawable.wizard_inline_collection_01,
            resGetString(R.string.wizard_inline_collection_title_1),
            resGetString(R.string.wizard_inline_collection_1)),
          WizardInlineData(
            R.drawable.wizard_inline_collection_02,
            resGetString(R.string.wizard_inline_collection_title_2),
            resGetString(R.string.wizard_inline_collection_2)),
          WizardInlineData(
            R.drawable.wizard_inline_collection_03,
            resGetString(R.string.wizard_inline_collection_title_3),
            resGetString(R.string.wizard_inline_collection_3)))
      case ProfileWizardInline =>
        Seq(
          WizardInlineData(
            R.drawable.wizard_inline_profile_01,
            resGetString(R.string.wizard_inline_profile_title_1),
            resGetString(R.string.wizard_inline_profile_1)),
          WizardInlineData(
            R.drawable.wizard_inline_profile_02,
            resGetString(R.string.wizard_inline_profile_title_2),
            resGetString(R.string.wizard_inline_profile_2)),
          WizardInlineData(
            R.drawable.wizard_inline_profile_03,
            resGetString(R.string.wizard_inline_profile_title_3),
            resGetString(R.string.wizard_inline_profile_3)))
    }

  // Styles

  private[this] def paginationItemStyle(implicit context: ContextWrapper): Tweak[ImageView] = {
    val size   = resGetDimensionPixelSize(R.dimen.wizard_size_pager)
    val margin = resGetDimensionPixelSize(R.dimen.wizard_margin_pager)
    lp[ViewGroup](size, size) +
      llLayoutMargin(margin, margin, margin, margin)
  }

}
