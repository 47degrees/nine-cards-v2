package cards.nine.commons.test.data.trackevent

import cards.nine.models.TrackEvent
import cards.nine.models.types._

trait WizardTrackEventTestData {

  val chooseAccountEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardStartCategory,
    action = ChooseAccountAction,
    label = None,
    value = None)

  val chooseNewConfigurationEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardConfigurationCategory,
    action = ChooseNewConfigurationAction,
    label = None,
    value = None)

  val chooseExistingDeviceEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardConfigurationCategory,
    action = ChooseExistingDeviceAction,
    label = None,
    value = None)

  val chooseMomentEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardMomentsWifiCategory,
    action = ChooseMomentAction,
    label = Option("OUT_AND_ABOUT"),
    value = None)

  val chooseMomentWifiEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardMomentsWifiCategory,
    action = ChooseMomentWifiAction,
    label = Option("OUT_AND_ABOUT"),
    value = None)

  val chooseOtherMomentEvent = TrackEvent(
    screen = WizardScreen,
    category = WizardOtherMomentsCategory,
    action = ChooseOtherMomentAction,
    label = Option("MUSIC"),
    value = None)

}
