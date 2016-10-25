package cards.nine.app.ui.launcher.actions.createoreditcollection

import cards.nine.app.ui.commons.{JobException, Jobs}
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.{CollectionData, Collection}
import cards.nine.models.types.FreeCollectionType
import macroid.ActivityContextWrapper

class CreateOrEditCollectionJobs(actions: CreateOrEditCollectionUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs {

  def initialize(maybeCollectionId: Option[String]): TaskService[Unit] = {

    def editCollection(collectionId: Int) =
      for {
        collection <- di.collectionProcess.getCollectionById(collectionId)
          .resolveOption(s"Can't find the collection with id $collectionId")
        _ <- actions.initializeEditCollection(collection)
      } yield ()

    for {
      theme <- getThemeTask
      _ <- actions.initialize(theme)
      _ <- maybeCollectionId match {
        case Some(collectionId) => editCollection(collectionId.toInt)
        case None => actions.initializeNewCollection()
      }
    } yield ()
  }

  def editCollection(collection: Collection, name: String, icon: String, themedColorIndex: Int): TaskService[Collection] = {
    val request = CollectionData(
      position = collection.position,
      name = name,
      collectionType = collection.collectionType,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = collection.appsCategory,
      cards = collection.cards map (_.toData),
      moment = collection.moment map (_.toData))
    for {
      collection <- di.collectionProcess.editCollection(collection.id, request)
      _ <- actions.close()
    } yield collection
  }


  def saveCollection(name: String, icon: String, themedColorIndex: Int): TaskService[Collection] = {
    val request = CollectionData(
      name = name,
      collectionType = FreeCollectionType,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = None,
      cards = Seq.empty,
      moment = None)
    for {
      collection <- di.collectionProcess.addCollection(request)
      _ <- actions.close()
    } yield collection
  }

  def updateIcon(maybeIcon: Option[String]): TaskService[Unit] =
    readOption(maybeIcon, "Empty index color")(actions.updateIcon)

  def updateColor(maybeIndexColor: Option[Int]): TaskService[Unit] =
    readOption(maybeIndexColor, "Empty index color")(actions.updateColor)

  def changeColor(maybeColor: Option[Int]): TaskService[Unit] =
    readOption(maybeColor, "Empty color")(actions.showColorDialog)

  def changeIcon(maybeIcon: Option[String]): TaskService[Unit] =
    readOption(maybeIcon, "Empty Icon")(actions.showIconDialog)

  private[this] def readOption[T](maybe: Option[T], errorMessage: String)
    (f: (T) => TaskService[Unit]): TaskService[Unit] =
    maybe match {
      case Some(value) => f(value)
      case None => TaskService.left(JobException(errorMessage))
    }

}