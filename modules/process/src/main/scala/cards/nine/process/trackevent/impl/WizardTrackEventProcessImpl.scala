package cards.nine.process.trackevent.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.models.TrackEvent
import cards.nine.models.types._
import cards.nine.process.trackevent.{ImplicitsTrackEventException, TrackEventException, TrackEventProcess}

trait WizardTrackEventProcessImpl extends TrackEventProcess {

  self: TrackEventDependencies with ImplicitsTrackEventException =>

  override def chooseAccount() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardStartCategory,
      action = ChooseAccountAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseNewConfiguration() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardConfigurationCategory,
      action = ChooseNewConfigurationAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseExistingDevice() = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardConfigurationCategory,
      action = ChooseExistingDeviceAction,
      label = None,
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseMoment(moment: NineCardsMoment) = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardMomentsWifiCategory,
      action = ChooseMomentAction,
      label = Option(moment.name),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseMomentWifi(moment: NineCardsMoment) = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardMomentsWifiCategory,
      action = ChooseMomentWifiAction,
      label = Option(moment.name),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

  override def chooseOtherMoment(moment: NineCardsMoment) = {
    val event = TrackEvent(
      screen = WizardScreen,
      category = WizardOtherMomentsCategory,
      action = ChooseOtherMomentAction,
      label = Option(moment.name),
      value = None)
    trackServices.trackEvent(event).resolve[TrackEventException]
  }

}
