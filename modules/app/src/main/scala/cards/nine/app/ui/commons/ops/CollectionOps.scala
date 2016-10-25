package cards.nine.app.ui.commons.ops

import cards.nine.models.{Collection, CollectionData}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

object CollectionOps {

  implicit class CollectionOp(collection: Collection) {

    def getUrlSharedCollection(implicit contextWrapper: ContextWrapper): Option[String] =
      collection.sharedCollectionId map { c =>
        resGetString(R.string.shared_collection_url, c)
      }

    def getUrlOriginalSharedCollection(implicit contextWrapper: ContextWrapper): Option[String] =
      collection.originalSharedCollectionId map { c =>
        resGetString(R.string.shared_collection_url, c)
      }

    def getIconWorkspace(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${collection.icon.toLowerCase}") getOrElse R.drawable.icon_collection_default

    def getIconDetail(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${collection.icon.toLowerCase}_detail") getOrElse R.drawable.icon_collection_default_detail

  }

  implicit class CollectionStringsOp(icon: String) {

    def getIconWorkspace(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${icon.toLowerCase}") getOrElse R.drawable.icon_collection_default

    def getIconDetail(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${icon.toLowerCase}_detail") getOrElse R.drawable.icon_collection_default_detail

  }

  implicit class CollectionDataOp(collection: CollectionData) {

    def getIconCollectionWorkspace(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${collection.icon.toLowerCase}") getOrElse R.drawable.icon_collection_default

    def getIconCollectionDetail(implicit context: ContextWrapper): Int =
      resGetDrawableIdentifier(s"icon_collection_${collection.icon.toLowerCase}_detail") getOrElse R.drawable.icon_collection_default_detail

    def getName(implicit contextWrapper: ContextWrapper): String =
      resGetString(collection.name.toLowerCase) getOrElse collection.name

  }

}
