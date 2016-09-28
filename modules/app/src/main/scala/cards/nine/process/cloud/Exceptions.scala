package com.fortysevendeg.ninecardslauncher.process.cloud

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.NineCardException

sealed trait CloudStorageError

case object SigInRequired extends CloudStorageError

case object RateLimitExceeded extends CloudStorageError

case object ResourceNotAvailable extends CloudStorageError

case class CloudStorageProcessException(message: String, cause: Option[Throwable] = None, driveError: Option[CloudStorageError] = None)
  extends RuntimeException(message)
  with NineCardException {

  cause foreach initCause

}