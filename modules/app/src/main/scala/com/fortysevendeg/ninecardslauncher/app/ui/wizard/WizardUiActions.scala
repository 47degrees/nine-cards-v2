package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import android.os.Build
import android.view.View
import android.widget._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.StepData
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.AnimatedWorkSpacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.StepsWorkspacesTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.widgets.tweaks.RippleBackgroundViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.models.{UserCloudDevice, UserCloudDevices}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._
import org.ocpsoft.prettytime.PrettyTime

class WizardUiActions(dom: WizardDOM)(implicit val context: ActivityContextWrapper, val uiContext: UiContext[_])
  extends WizardStyles {

  val newConfigurationKey = "new_configuration"

  lazy val steps = Seq(
    StepData(R.drawable.wizard_01, resGetString(R.string.wizard_step_1)),
    StepData(R.drawable.wizard_02, resGetString(R.string.wizard_step_2)),
    StepData(R.drawable.wizard_03, resGetString(R.string.wizard_step_3)),
    StepData(R.drawable.wizard_04, resGetString(R.string.wizard_step_4)),
    StepData(R.drawable.wizard_05, resGetString(R.string.wizard_step_5)))

  def initialize(jobs: WizardJobs): TaskService[Unit] = {

    def pagination(position: Int) =
      (w[ImageView] <~ paginationItemStyle <~ vTag(position.toString)).get

    def createPagers(steps: Seq[StepData]) = {
      val pagerViews = steps.indices map { position =>
        val view = pagination(position)
        view.setActivated(position == 0)
        view
      }
      dom.paginationPanel <~ vgAddViews(pagerViews)
    }

    def reloadPagers(currentPage: Int) = Transformer {
      case i: ImageView if Option(i.getTag).isDefined && i.getTag.equals(currentPage.toString) => i <~ vActivated(true)
      case i: ImageView => i <~ vActivated(false)
    }

    def initializeUi(): Ui[Any] =
      (dom.userAction <~
        defaultActionStyle <~
        On.click {
          Ui {
            val termsAccept = dom.usersTerms.isChecked
            jobs.connectAccount(termsAccept).resolveAsync()
          }
        }) ~
        (dom.deviceAction <~
          defaultActionStyle <~
          On.click {
            dom.devicesGroup <~ Transformer {
              case i: RadioButton if i.isChecked =>
                Ui {
                  val tag = Option(i.getTag) map (_.toString)
                  tag match {
                    case Some(`newConfigurationKey`) => jobs.deviceSelected(None).resolveAsyncServiceOr(_ => goToUser())
                    case cloudId => jobs.deviceSelected(cloudId).resolveAsyncServiceOr(_ => goToUser())
                  }
                }
            }
          }) ~
        (dom.workspaces <~
          vGlobalLayoutListener(_ => {
            dom.workspaces <~
              swData(steps) <~
              awsAddPageChangedObserver(currentPage => {
                val backgroundColor = resGetColor(s"wizard_background_step_$currentPage") getOrElse resGetColor(R.color.primary)
                ((dom.wizardRootLayout <~ rbvColor(backgroundColor)) ~
                  (dom.stepsAction <~ (if (currentPage == steps.length - 1) vVisible else vInvisible)) ~
                  (dom.paginationPanel <~ reloadPagers(currentPage))).run
              })
          })) ~
        (dom.stepsAction <~
          diveInActionStyle <~
          On.click(Ui(jobs.finishWizard().resolveAsync()))) ~
        createPagers(steps)

    for {
      _ <- initializeUi().toService
      _ <- goToUser()
    } yield ()
  }

  def goToUser(): TaskService[Unit] =
    ((dom.loadingRootLayout <~ vInvisible) ~
      (dom.userRootLayout <~ vVisible) ~
      (dom.wizardRootLayout <~ vInvisible) ~
      (dom.deviceRootLayout <~ vInvisible)).toService

  def goToWizard(): TaskService[Unit] =
    ((dom.loadingRootLayout <~ vInvisible) ~
      (dom.userRootLayout <~ vInvisible) ~
      (dom.wizardRootLayout <~ vVisible <~ rbvColor(resGetColor(R.color.wizard_background_step_0), forceFade = true)) ~
      (dom.deviceRootLayout <~ vInvisible)).toService

  def showLoading(): TaskService[Unit] =
    ((dom.loadingRootLayout <~ vVisible) ~
      (dom.userRootLayout <~ vInvisible) ~
      (dom.wizardRootLayout <~ vInvisible) ~
      (dom.deviceRootLayout <~ vInvisible)).toService

  def showErrorLoginUser(): TaskService[Unit] = backToUser(R.string.errorLoginUser)

  def showErrorConnectingGoogle(): TaskService[Unit] = backToUser(R.string.errorConnectingGoogle)

  private[this] def backToUser(errorMessage: Int): TaskService[Unit] =
    for {
      _ <- uiShortToast2(errorMessage).toService
      _ <- goToUser()
    } yield ()

  def showErrorAcceptTerms(): TaskService[Unit] =
    (dom.rootLayout <~ vSnackbarShort(R.string.messageAcceptTerms)).toService

  def showDevices(devices: UserCloudDevices): TaskService[Unit] = {

    def subtitle(device: UserCloudDevice): String = {
      if (device.fromV1) resGetString(R.string.deviceMigratedFromV1) else {
        val time = new PrettyTime().format(device.modifiedDate)
        resGetString(R.string.syncLastSynced, time)
      }
    }

    def userRadio(title: String, tag: String, visible: Boolean = true): RadioButton =
      (w[RadioButton] <~
        radioStyle <~
        tvText(title) <~
        vTag(tag) <~
        (if (visible) vVisible else vGone)).get

    def userRadioSubtitle(text: String, visible: Boolean = true): TextView =
      (w[TextView] <~
        radioSubtitleStyle <~
        tvText(text) <~
        (if (visible) vVisible else vGone)).get

    def otherDevicesLink(text: String): TextView =
      (w[TextView] <~
        otherDevicesLinkStyle <~
        tvUnderlineText(text) <~
        FuncOn.click { v: View =>
          (dom.devicesGroup <~ Transformer {
            case view if view.getVisibility == View.GONE => view <~ vVisible
            case _ => Ui.nop
          }) ~ (v <~ vGone)
        }).get

    def addDevicesToRadioGroup(): Ui[Any] = {

      val userRadioView = devices.userDevice.toSeq.flatMap { device =>
        Seq(
          userRadio(resGetString(R.string.currentDeviceTitle, device.deviceName), device.cloudId),
          userRadioSubtitle(subtitle(device)))
      }

      val newConfRadioView = Seq(
        userRadio(resGetString(R.string.loadUserConfigDeviceReplace, Build.MODEL), newConfigurationKey),
        userRadioSubtitle(resGetString(R.string.newConfigurationSubtitle)))

      val allRadioViews = {

        val radioViews = devices.devices flatMap { device =>
          Seq(
            userRadio(device.deviceName, device.cloudId, visible = false),
            userRadioSubtitle(subtitle(device), visible = false))
        }

        if (radioViews.isEmpty) radioViews else {
          otherDevicesLink(resGetString(R.string.otherDevicesLink)) +: radioViews
        }
      }

      val radioViews = userRadioView ++ newConfRadioView ++ allRadioViews

      (dom.devicesGroup <~ vgRemoveAllViews <~ vgAddViews(radioViews)) ~
        Ui {
          radioViews.headOption match {
            case Some(radioButton: RadioButton) => radioButton.setChecked(true)
            case _ =>
          }
        }
    }

    def showDevices(): Ui[Any] =
      (dom.loadingRootLayout <~ vGone) ~
        (dom.userRootLayout <~ vGone) ~
        (dom.wizardRootLayout <~ vGone) ~
        (dom.deviceRootLayout <~ vVisible)

    for {
      _ <- addDevicesToRadioGroup().toService
      _ <- showDevices().toService
      _ <- (dom.titleDevice <~ tvText(resGetString(R.string.addDeviceTitle, devices.name))).toService
    } yield ()
  }

  def showDiveIn(): TaskService[Unit] = (dom.stepsAction <~ vEnabled(true)).toService

}

