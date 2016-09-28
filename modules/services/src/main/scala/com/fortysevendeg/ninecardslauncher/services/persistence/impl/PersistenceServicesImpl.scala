package cards.nine.services.persistence.impl

import cards.nine.repository.repositories._
import cards.nine.services.persistence._
import cards.nine.services.persistence.conversions.Conversions

class PersistenceServicesImpl(
  val appRepository: AppRepository,
  val cardRepository: CardRepository,
  val collectionRepository: CollectionRepository,
  val dockAppRepository: DockAppRepository,
  val momentRepository: MomentRepository,
  val userRepository: UserRepository,
  val widgetRepository: WidgetRepository)
  extends PersistenceServices
  with Conversions
  with PersistenceDependencies
  with AppPersistenceServicesImpl
  with CardPersistenceServicesImpl
  with CollectionPersistenceServicesImpl
  with DockAppPersistenceServicesImpl
  with MomentPersistenceServicesImpl
  with UserPersistenceServicesImpl
  with WidgetPersistenceServicesImpl
  with AndroidPersistenceServicesImpl
  with ImplicitsPersistenceServiceExceptions