package cards.nine.services.persistence.impl

import cards.nine.commons.services.TaskService._
import cards.nine.commons.NineCardExtensions._
import cards.nine.services.persistence._
import cards.nine.services.persistence.conversions.Conversions

trait UserPersistenceServicesImpl extends PersistenceServices {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def addUser(request: AddUserRequest) =
    (for {
      user <- userRepository.addUser(toRepositoryUserData(request))
    } yield toUser(user)).resolve[PersistenceServiceException]

  def deleteAllUsers() =
    (for {
      deleted <- userRepository.deleteUsers()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteUser(request: DeleteUserRequest) =
    (for {
      deleted <- userRepository.deleteUser(toRepositoryUser(request.user))
    } yield deleted).resolve[PersistenceServiceException]

  def fetchUsers =
    (for {
      userItems <- userRepository.fetchUsers
    } yield userItems map toUser).resolve[PersistenceServiceException]

  def findUserById(request: FindUserByIdRequest) =
    (for {
      maybeUser <- userRepository.findUserById(request.id)
    } yield maybeUser map toUser).resolve[PersistenceServiceException]

  def updateUser(request: UpdateUserRequest) =
    (for {
      updated <- userRepository.updateUser(toRepositoryUser(request))
    } yield updated).resolve[PersistenceServiceException]
}
