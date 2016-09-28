package com.fortysevendeg.ninecardslauncher.app.ui.commons.ops

import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.process.commons.models.PrivateCollection
import com.fortysevendeg.ninecardslauncher2.R
import macroid.ContextWrapper

object PrivateCollectionOps {

  implicit class PrivateCollectionOp(privateCollection: PrivateCollection) {

    def getIconCollectionWorkspace(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${privateCollection.icon.toLowerCase}") getOrElse R.drawable.icon_collection_default

    def getIconCollectionDetail(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${privateCollection.icon.toLowerCase}_detail") getOrElse R.drawable.icon_collection_default_detail

    def getName(implicit contextWrapper: ContextWrapper): String =
      resGetString(privateCollection.name.toLowerCase) getOrElse privateCollection.name

  }

}
