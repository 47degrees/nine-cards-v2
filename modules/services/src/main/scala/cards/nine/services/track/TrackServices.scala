package com.fortysevendeg.ninecardslauncher.services.track

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._

trait TrackServices {

  /**
    * Track event in Google Analytics
    * @throws TrackServicesException if there was an error with the request GoogleAnalytics
    */
  def trackEvent(event: TrackEvent): TaskService[Unit]
}
