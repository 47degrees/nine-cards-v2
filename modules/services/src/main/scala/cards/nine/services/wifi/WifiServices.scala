package com.fortysevendeg.ninecardslauncher.services.wifi

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.TaskService

trait WifiServices {

  /**
    * Get the current SSID if it is available
    *
    * @return an Option[String] that contains the name of the SSID
    * @throws WifiServicesException if exist some problem to get the current SSID
    */
  def getCurrentSSID(implicit contextSupport: ContextSupport): TaskService[Option[String]]

  /**
    * Get all configured networks sorted by name
    *
    * @return Seq[String] that contains all SSIDs
    * @throws WifiServicesException if exist some problem getting the information
    */
  def getConfiguredNetworks(implicit contextSupport: ContextSupport): TaskService[Seq[String]]

}
