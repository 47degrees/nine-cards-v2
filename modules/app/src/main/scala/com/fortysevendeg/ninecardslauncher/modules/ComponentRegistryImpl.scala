package com.fortysevendeg.ninecardslauncher.modules

import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.modules.api.impl.ApiServicesComponentImpl
import com.fortysevendeg.ninecardslauncher.modules.appsmanager.impl.AppManagerServicesComponentImpl
import com.fortysevendeg.ninecardslauncher.modules.image.impl.ImageServicesComponentImpl
import com.fortysevendeg.ninecardslauncher.modules.repository.impl.RepositoryServicesComponentImpl

trait ComponentRegistryImpl
  extends ComponentRegistry
  with AppContextProvider
  with ImageServicesComponentImpl
  with AppManagerServicesComponentImpl
  with RepositoryServicesComponentImpl
  with ApiServicesComponentImpl

