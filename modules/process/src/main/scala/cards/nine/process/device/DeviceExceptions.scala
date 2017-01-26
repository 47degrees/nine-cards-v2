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

package cards.nine.process.device

import cards.nine.commons.services.TaskService.NineCardException

case class ResetException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class AppException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class CreateBitmapException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class ShortcutException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class ContactException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class ContactPermissionException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class WidgetException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class CallException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class CallPermissionException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class DeviceException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class DockAppException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

trait ImplicitsDeviceException {
  implicit def resetException = (t: Throwable) => ResetException(t.getMessage, Option(t))

  implicit def appException = (t: Throwable) => AppException(t.getMessage, Option(t))

  implicit def createBitmapException =
    (t: Throwable) => CreateBitmapException(t.getMessage, Option(t))

  implicit def shortcutException = (t: Throwable) => ShortcutException(t.getMessage, Option(t))

  implicit def contactException = (t: Throwable) => ContactException(t.getMessage, Option(t))

  implicit def widgetException = (t: Throwable) => WidgetException(t.getMessage, Option(t))

  implicit def callException = (t: Throwable) => CallException(t.getMessage, Option(t))

  implicit def dockAppException = (t: Throwable) => DockAppException(t.getMessage, Option(t))

  implicit def deviceException = (t: Throwable) => DeviceException(t.getMessage, Option(t))
}
