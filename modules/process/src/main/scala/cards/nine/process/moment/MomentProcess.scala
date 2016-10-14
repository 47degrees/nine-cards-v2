package cards.nine.process.moment

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.process.commons.models.{Collection, Moment, MomentWithCollection}
import cards.nine.models.types.NineCardsMoment

trait MomentProcess {

  /**
    * Gets the existing moments
    *
    * @return the Seq[cards.nine.process.moment.models.Moment]
    * @throws MomentException if there was an error getting the existing moments
    */
  def getMoments: TaskService[Seq[Moment]]

  /**
    * Get moment by type, if the moment don't exist return an exception
    *
    * @param momentType type of moment
    * @return the cards.nine.process.moment.models.Moment
    * @throws MomentException if there was an error getting the existing moments
    */
  def getMomentByType(momentType: NineCardsMoment): TaskService[Moment]

  /**
    * Get moment by type, if the moment don't exist return None
    *
    * @param momentType type of moment
    * @return the cards.nine.process.moment.models.Moment
    * @throws MomentException if there was an error getting the existing moments
    */
  def fetchMomentByType(momentType: NineCardsMoment): TaskService[Option[Moment]]

  /**
    * Create new Moment without collection by type
    *
    * @return the List[cards.nine.process.commons.models.Collection]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def createMomentWithoutCollection(nineCardsMoment: NineCardsMoment)(implicit context: ContextSupport): TaskService[Moment]

  /**
    * Creates Moments from some already formed and given Moments
    *
    * @param item the cards.nine.process.moment.UpdateMomentRequest of Moments
    * @return Unit
    * @throws MomentException if there was an error creating the moments' collections
    */
  def updateMoment(item: UpdateMomentRequest)(implicit context: ContextSupport): TaskService[Unit]

  /**
    * Creates Moments from some already formed and given Moments
    *
    * @param items the Seq[cards.nine.process.moment.SaveMomentRequest] of Moments
    * @return the List[cards.nine.process.moment.models.Moment]
    * @throws MomentException if there was an error creating the moments' collections
    */
  def saveMoments(items: Seq[SaveMomentRequest])(implicit context: ContextSupport): TaskService[Seq[Moment]]

  /**
    * Delete all moments in database
    *
    * @throws MomentException if exist some problem to get the app or storing it
    */
  def deleteAllMoments(): TaskService[Unit]

  /**
    * Gets the best available moments
    *
    * @return the best cards.nine.process.moment.models.Moment
    * @throws MomentException if there was an error getting the best moment
    */
  def getBestAvailableMoment(implicit context: ContextSupport): TaskService[Option[Moment]]

  /**
    * Gets all available moments. Only the moments with collection
    *
    * @return sequuence cards.nine.process.moment.models.Moment
    * @throws MomentException if there was an error getting the best moment
    */
  def getAvailableMoments(implicit context: ContextSupport): TaskService[Seq[MomentWithCollection]]

}
