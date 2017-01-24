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

package cards.nine.services.persistence.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.WidgetTestData
import cards.nine.commons.test.data.WidgetValues._
import cards.nine.models.Widget
import cards.nine.repository.RepositoryException
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.services.persistence.data.WidgetPersistenceServicesData
import monix.eval.Task
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification

trait WidgetPersistenceServicesSpecification extends Specification with DisjunctionMatchers {

  trait WidgetPersistenceServicesScope
      extends RepositoryServicesScope
      with WidgetTestData
      with WidgetPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class WidgetPersistenceServicesImplSpec extends WidgetPersistenceServicesSpecification {

  "addWidget" should {

    "return a Widget for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.addWidget(any) returns TaskService(Task(Either.right(repoWidget)))
      val result = persistenceServices.addWidget(widgetData).value.run

      result must beLike {
        case Right(widget) =>
          widget.id shouldEqual widgetId
          widget.packageName shouldEqual widgetPackageName
      }
    }

    "return Unit for a valid request when AppWidgetId is None" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.addWidget(any) returns
        TaskService(
          Task(Either.right(repoWidget.copy(data = repoWidgetData.copy(appWidgetId = 0)))))
      val result = persistenceServices.addWidget(widgetData).value.run

      result must beLike {
        case Right(widget) =>
          widget.id shouldEqual widgetId
          widget.packageName shouldEqual widgetPackageName
          widget.appWidgetId shouldEqual None
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {
      mockWidgetRepository.addWidget(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addWidget(widgetData).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "addWidgets" should {

    "return the sequence of widgets added" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.right(seqRepoWidget)))
      val result = persistenceServices.addWidgets(seqWidgetData).value.run
      result shouldEqual Right(seqWidget)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addWidgets(seqWidgetData).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteAllWidgets" should {

    "return the number of elements deleted for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets() returns TaskService(Task(Either.right(deletedWidgets)))
      val result = persistenceServices.deleteAllWidgets().value.run
      result shouldEqual Right(deletedWidgets)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets() returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteAllWidgets().value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteWidget" should {

    "return the number of elements deleted for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidget(any) returns TaskService(Task(Either.right(deletedWidget)))
      val result = persistenceServices.deleteWidget(widget).value.run
      result shouldEqual Right(deletedWidget)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidget(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteWidget(widget).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteWidgetsByMoment" should {

    "return the number of elements deleted for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets(any) returns TaskService(
        Task(Either.right(deletedWidgets)))
      val result = persistenceServices.deleteWidgetsByMoment(widgetMomentId).value.run
      result shouldEqual Right(deletedWidgets)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.deleteWidgets(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteWidgetsByMoment(widgetMomentId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchWidgetByAppWidgetId" should {

    "return a Widget elements for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetByAppWidgetId(any) returns TaskService(
        Task(Either.right(Some(repoWidget))))
      val result = persistenceServices.fetchWidgetByAppWidgetId(appWidgetId).value.run
      result must beLike {
        case Right(widgets) => widgets.size shouldEqual deletedWidget
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetByAppWidgetId(any) returns TaskService(
        Task(Either.left(exception)))
      val result = persistenceServices.fetchWidgetByAppWidgetId(appWidgetId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchWidgetsByMoment" should {

    "return a list of Widget elements for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetsByMoment(any) returns TaskService(
        Task(Either.right(seqRepoWidget)))
      val result = persistenceServices.fetchWidgetsByMoment(widgetMomentId).value.run

      result must beLike {
        case Right(widgets) => widgets.size shouldEqual seqWidget.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgetsByMoment(any) returns TaskService(
        Task(Either.left(exception)))
      val result = persistenceServices.fetchWidgetsByMoment(widgetMomentId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchWidgets" should {

    "return a list of Widget elements for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgets() returns TaskService(Task(Either.right(seqRepoWidget)))
      val result = persistenceServices.fetchWidgets.value.run

      result must beLike {
        case Right(widgets) => widgets.size shouldEqual seqWidget.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.fetchWidgets() returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchWidgets.value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "findWidgetById" should {

    "return a Widget for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.findWidgetById(any) returns TaskService(
        Task(Either.right(Option(repoWidget))))
      val result = persistenceServices.findWidgetById(widgetId).value.run

      result must beLike {
        case Right(maybeWidget) =>
          maybeWidget must beSome[Widget].which { widget =>
            widget.id shouldEqual widgetId
          }
      }
    }

    "return None when a non-existent id is given" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.findWidgetById(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.findWidgetById(nonExistentWidgetId).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.findWidgetById(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findWidgetById(widgetId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "updateWidget" should {

    "return the number of elements updated for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidget(any) returns TaskService(Task(Either.right(deletedWidget)))
      val result = persistenceServices.updateWidget(widget).value.run
      result shouldEqual Right(deletedWidget)
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidget(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateWidget(widget).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "updateWidgets" should {

    "return the sequence with the number of elements updated for a valid request" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidgets(any) returns TaskService(
        Task(Either.right(Seq(updatedWidgets))))
      val result = persistenceServices.updateWidgets(seqWidget).value.run
      result shouldEqual Right(Seq(updatedWidgets))
    }

    "return a PersistenceServiceException if the service throws a exception" in new WidgetPersistenceServicesScope {

      mockWidgetRepository.updateWidgets(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateWidgets(seqWidget).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }
}
