package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toDockApp
import com.fortysevendeg.ninecardslauncher.repository.model.{DockApp, DockAppData}
import com.fortysevendeg.ninecardslauncher.repository.provider.DockAppEntity
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.provider.DockAppEntity._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import IterableCursor._

import scalaz.concurrent.Task

class DockAppRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val dockAppUri = uriCreator.parse(dockAppUriString)

  val dockAppNotificationUri = uriCreator.parse(dockAppUriNotificationString)

  def addDockApp(data: DockAppData): ServiceDef2[DockApp, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            name -> data.name,
            dockType -> data.dockType,
            intent -> data.intent,
            imagePath -> data.imagePath,
            position -> data.position)

          val id = contentResolverWrapper.insert(
            uri = dockAppUri,
            values = values,
            notificationUri = Some(dockAppNotificationUri))

          DockApp(id = id, data = data)
        }
      }
    }

  def deleteDockApps(where: String = ""): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = dockAppUri,
            where = where,
            notificationUri = Some(dockAppNotificationUri))
        }
      }
    }

  def deleteDockApp(dockApp: DockApp): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = dockAppUri,
            id = dockApp.id,
            notificationUri = Some(dockAppNotificationUri))
        }
      }
    }

  def findDockAppById(id: Int): ServiceDef2[Option[DockApp], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = dockAppUri,
            id = id,
            projection = allFields)(getEntityFromCursor(dockAppEntityFromCursor)) map toDockApp
        }
      }
    }

  def fetchDockApps(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = s"${DockAppEntity.position} asc"): ServiceDef2[Seq[DockApp], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = dockAppUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy)(getListFromCursor(dockAppEntityFromCursor)) map toDockApp
        }
      }
    }

  def fetchIterableDockApps(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = s"${DockAppEntity.position} asc"): ServiceDef2[IterableCursor[DockApp], RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = dockAppUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(dockAppFromCursor)
        }
      }
    }

  def updateDockApp(item: DockApp): ServiceDef2[Int, RepositoryException] =
    Service {
      Task {
        CatchAll[RepositoryException] {
          val values = Map[String, Any](
            name -> item.data.name,
            dockType -> item.data.dockType,
            intent -> item.data.intent,
            imagePath -> item.data.imagePath,
            position -> item.data.position)

          contentResolverWrapper.updateById(
            uri = dockAppUri,
            id = item.id,
            values = values,
            notificationUri = Some(dockAppNotificationUri))
        }
      }
    }
}
