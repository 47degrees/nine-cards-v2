package cards.nine.services.shortcuts.impl

import cards.nine.services.shortcuts.models.Shortcut

trait ShortcutsServicesImplData {

  val sampleShortcut1 =  Shortcut(
    title = "B - Sample Name 1",
    icon = 0,
    name = "Name 1",
    packageName = "cards.nine.test.sampleapp1")

  val sampleShortcut2 =  Shortcut(
    title = "A - Sample Name 2",
    icon = 0,
    name = "Name 2",
    packageName = "cards.nine.test.sampleapp2")

  val shotcutsList = Seq(sampleShortcut1, sampleShortcut2)

}
