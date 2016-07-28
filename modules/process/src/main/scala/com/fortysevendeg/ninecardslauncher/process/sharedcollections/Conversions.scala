package com.fortysevendeg.ninecardslauncher.process.sharedcollections

import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{SharedCollectionPackage, SharedCollection, CreatedCollection}
import com.fortysevendeg.ninecardslauncher.services.api.{SharedCollectionPackageResponse, SharedCollectionResponse, CreateSharedCollectionResponse}

trait Conversions {

  def toSharedCollection(item: SharedCollectionResponse): SharedCollection =
    SharedCollection(
      id = item.id,
      sharedCollectionId = item.sharedCollectionId,
      publishedOn = item.publishedOn,
      description = item.description,
      screenshots = item.screenshots,
      author = item.author,
      tags = item.tags,
      name = item.name,
      shareLink = item.shareLink,
      packages = item.packages,
      resolvedPackages = item.resolvedPackages map toSharedCollectionPackage,
      views = item.views,
      category = NineCardCategory(item.category),
      icon = item.icon,
      community = item.community)

  def toSharedCollectionPackage(item: SharedCollectionPackageResponse): SharedCollectionPackage =
    SharedCollectionPackage(
      packageName = item.packageName,
      title = item.title,
      description = item.description,
      icon = item.icon,
      stars = item.stars,
      downloads = item.downloads,
      free = item.free)

  def toCreatedCollection(item: CreateSharedCollectionResponse): CreatedCollection =
    CreatedCollection(
      name = item.newSharedCollection.name,
      description = item.newSharedCollection.description,
      author = item.newSharedCollection.author,
      packages = item.newSharedCollection.packages,
      category = NineCardCategory(item.newSharedCollection.category),
      shareLink = item.newSharedCollection.shareLink,
      icon = item.newSharedCollection.icon,
      community = item.newSharedCollection.community
    )
}
