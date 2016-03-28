package com.fortysevendeg.ninecardslauncher.process.moment

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection

trait MomentProcess {

  /**
    * Creates Moments and their associated Collections with the apps installed in the device
    * @return the List[com.fortysevendeg.ninecardslauncher.process.collection.models.Collection]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def createMoments(implicit context: ContextSupport): ServiceDef2[List[Collection], MomentException]

  /**
    * Delete all moments in database
    * @throws MomentException if exist some problem to get the app or storing it
    */
  def deleteAllMoments(): ServiceDef2[Unit, MomentException]

}
