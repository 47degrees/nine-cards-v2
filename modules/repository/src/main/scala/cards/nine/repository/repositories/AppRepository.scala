/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.repository.repositories

import cards.nine.commons.CatchAll
import cards.nine.commons.contentresolver.Conversions._
import cards.nine.commons.contentresolver.NotificationUri._
import cards.nine.commons.contentresolver.{ContentResolverWrapper, UriCreator}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.IterableCursor
import cards.nine.models.IterableCursor._
import cards.nine.repository.Conversions.toApp
import cards.nine.repository.model.{App, AppData, DataCounter}
import cards.nine.repository.provider.AppEntity._
import cards.nine.repository.provider.NineCardsUri._
import cards.nine.repository.provider.{AppEntity, NineCardsUri}
import cards.nine.repository.{ImplicitsRepositoryExceptions, RepositoryException}
import org.joda.time.DateTime

class AppRepository(contentResolverWrapper: ContentResolverWrapper, uriCreator: UriCreator)
    extends ImplicitsRepositoryExceptions {

  val appUri = uriCreator.parse(appUriString)

  val appNotificationUri = uriCreator.parse(s"$baseUriNotificationString/$appUriPath")

  val abc = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ"

  val wildcard = "#"

  val game = "GAME"

  def addApp(data: AppData): TaskService[App] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = createMapValues(data)

        val id = contentResolverWrapper
          .insert(uri = appUri, values = values, notificationUris = Seq(appNotificationUri))

        App(id = id, data = data)
      }
    }

  def addApps(datas: Seq[AppData]): TaskService[Unit] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = datas map createMapValues

        contentResolverWrapper.inserts(
          authority = NineCardsUri.authorityPart,
          uri = appUri,
          allValues = values,
          notificationUris = Seq(appNotificationUri))
      }
    }

  def deleteApps(where: String = ""): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper
          .delete(uri = appUri, where = where, notificationUris = Seq(appNotificationUri))
      }
    }

  def deleteApp(app: App): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper
          .deleteById(uri = appUri, id = app.id, notificationUris = Seq(appNotificationUri))
      }
    }

  def deleteAppByPackage(packageName: String): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.delete(
          uri = appUri,
          where = s"${AppEntity.packageName} = ?",
          whereParams = Seq(packageName),
          notificationUris = Seq(appNotificationUri))
      }
    }

  def fetchApps(orderBy: String = ""): TaskService[Seq[App]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.fetchAll(uri = appUri, projection = allFields, orderBy = orderBy)(
          getListFromCursor(appEntityFromCursor)) map toApp
      }
    }

  def fetchIterableApps(
      where: String = "",
      whereParams: Seq[String] = Seq.empty,
      orderBy: String = ""): TaskService[IterableCursor[App]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper
          .getCursor(
            uri = appUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy)
          .toIterator(appFromCursor)
      }
    }

  def fetchAlphabeticalAppsCounter: TaskService[Seq[DataCounter]] =
    toDataCounter(
      fetchData = getNamesAlphabetically,
      normalize = (name: String) =>
        name.substring(0, 1).toUpperCase match {
          case t if abc.contains(t) => t
          case _                    => wildcard
      })

  def fetchCategorizedAppsCounter: TaskService[Seq[DataCounter]] =
    toDataCounter(fetchData = getCategoriesAlphabetically, normalize = {
      case t if t.startsWith(game) => game
      case t                       => t
    })

  def fetchInstallationDateAppsCounter: TaskService[Seq[DataCounter]] =
    toInstallationDateDataCounter(fetchData = getInstallationDate)

  def findAppById(id: Int): TaskService[Option[App]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.findById(uri = appUri, id = id, projection = allFields)(
          getEntityFromCursor(appEntityFromCursor)) map toApp
      }
    }

  def fetchAppByPackage(packageName: String): TaskService[Option[App]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.fetch(
          uri = appUri,
          projection = allFields,
          where = s"${AppEntity.packageName} = ?",
          whereParams = Seq(packageName))(getEntityFromCursor(appEntityFromCursor)) map toApp
      }
    }

  def fetchAppByPackages(packageNames: Seq[String]): TaskService[Seq[App]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper
          .fetchAll(
            uri = appUri,
            projection = allFields,
            where =
              s"${AppEntity.packageName} IN (${packageNames.map(p => s"'$p'").mkString(",")})")(
            getListFromCursor(appEntityFromCursor)) map toApp
      }
    }

  def fetchAppsByCategory(category: String, orderBy: String = ""): TaskService[Seq[App]] =
    TaskService {
      CatchAll[RepositoryException] {
        val (where, param) = whereCategory(category)
        contentResolverWrapper.fetchAll(
          uri = appUri,
          projection = allFields,
          where = where,
          whereParams = Seq(param),
          orderBy = orderBy)(getListFromCursor(appEntityFromCursor)) map toApp
      }
    }

  def fetchIterableAppsByCategory(
      category: String,
      orderBy: String = ""): TaskService[IterableCursor[App]] =
    TaskService {
      CatchAll[RepositoryException] {
        val (where, param) = whereCategory(category)
        contentResolverWrapper
          .getCursor(
            uri = appUri,
            projection = allFields,
            where = where,
            whereParams = Seq(param),
            orderBy = orderBy)
          .toIterator(appFromCursor)
      }
    }

  def updateApp(app: App): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = createMapValues(app.data)

        contentResolverWrapper.updateById(
          uri = appUri,
          id = app.id,
          values = values,
          notificationUris = Seq(appNotificationUri)
        )
      }
    }

  protected def getNamesAlphabetically: Seq[String] =
    getListFromCursor(nameFromCursor)(
      contentResolverWrapper
        .getCursor(uri = appUri, projection = Seq(name), orderBy = s"$name COLLATE NOCASE ASC"))

  protected def getCategoriesAlphabetically: Seq[String] =
    getListFromCursor(categoryFromCursor)(
      contentResolverWrapper.getCursor(
        uri = appUri,
        projection = Seq(category),
        orderBy = s"$category COLLATE NOCASE ASC"))

  protected def getInstallationDate: Seq[Long] =
    getListFromCursor(dateInstalledFromCursor)(contentResolverWrapper
      .getCursor(uri = appUri, projection = Seq(dateInstalled), orderBy = s"$dateInstalled DESC"))

  private[this] def whereCategory(category: String): (String, String) = category match {
    case t if t.startsWith(game) =>
      (s"${AppEntity.category} LIKE ?", s"$category%")
    case _ =>
      (s"${AppEntity.category} = ?", category)
  }

  private[this] def toDataCounter(
      fetchData: => Seq[String],
      normalize: (String) => String = (term) => term): TaskService[Seq[DataCounter]] =
    TaskService {
      CatchAll[RepositoryException] {
        val data = fetchData
        data.foldLeft(Seq.empty[DataCounter]) { (acc, name) =>
          val term = normalize(name)
          val lastWithSameTerm = acc.lastOption flatMap {
            case last if last.term == term => Some(last)
            case _                         => None
          }
          lastWithSameTerm map { c =>
            acc.dropRight(1) :+ c.copy(count = c.count + 1)
          } getOrElse acc :+ DataCounter(term, 1)
        }
      }
    }

  private[this] def toInstallationDateDataCounter(
      fetchData: => Seq[Long]): TaskService[Seq[DataCounter]] =
    TaskService {
      CatchAll[RepositoryException] {
        val now            = new DateTime()
        val moreOfTwoMoths = "moreOfTwoMoths"
        val dates = Seq(
          InstallationDateInterval("oneWeek", now.minusWeeks(1)),
          InstallationDateInterval("twoWeeks", now.minusWeeks(2)),
          InstallationDateInterval("oneMonth", now.minusMonths(1)),
          InstallationDateInterval("twoMonths", now.minusMonths(2)),
          InstallationDateInterval("fourMonths", now.minusMonths(4)),
          InstallationDateInterval("sixMonths", now.minusMonths(6)))
        val data = fetchData
        data.foldLeft(Seq.empty[DataCounter]) { (acc, date) =>
          val installationDate = new DateTime(date)
          val term             = termInterval(installationDate, dates) map (_.term) getOrElse moreOfTwoMoths
          val lastWithSameTerm = acc.lastOption flatMap {
            case last if last.term == term => Some(last)
            case _                         => None
          }
          lastWithSameTerm map { c =>
            acc.dropRight(1) :+ c.copy(count = c.count + 1)
          } getOrElse acc :+ DataCounter(term, 1)
        }
      }
    }

  private[this] def termInterval(
      installationDate: DateTime,
      intervals: Seq[InstallationDateInterval]): Option[InstallationDateInterval] =
    intervals find { interval =>
      installationDate.isAfter(interval.date)
    }

  private[this] def createMapValues(data: AppData) =
    Map[String, Any](
      name                    -> data.name,
      packageName             -> data.packageName,
      className               -> data.className,
      category                -> data.category,
      dateInstalled           -> data.dateInstalled,
      dateUpdate              -> data.dateUpdate,
      version                 -> data.version,
      installedFromGooglePlay -> data.installedFromGooglePlay)

  case class InstallationDateInterval(term: String, date: DateTime)

}
