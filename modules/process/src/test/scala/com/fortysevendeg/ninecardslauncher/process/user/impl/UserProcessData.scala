package com.fortysevendeg.ninecardslauncher.process.user.impl

import com.fortysevendeg.ninecardslauncher.process.user.models.Device
import com.fortysevendeg.ninecardslauncher.services.api.models.{AndroidDevice, GoogleDevice, Installation, User}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait UserProcessData
  extends Scope
  with Mockito {

  val statusCodeUser = 101

  val statusCodeOk = 200

  val userId = "fake-user-id"

  val userToken = "fake-user-token"

  val email = "example@47deg.com"

  val device = Device(
    "Nexus X",
    "",
    "",
    Seq.empty)

  val googleDevice = GoogleDevice(
    device.name,
    device.deviceId,
    device.secretToken,
    device.permissions)

  val user = User(
    Option(userId),
    Option(userToken),
    Option(email),
    Seq(googleDevice))

  val installationStatusCode = 102

  val installationId = "fake-installation-id"
  val installationToken = "fake-user-token"
  val deviceType = Some(AndroidDevice)

  val initialInstallation = Installation(None, deviceType, None, None)

  val installation = Installation(
    Option(installationId),
    deviceType,
    Option(installationToken),
    Option(userId)
  )

  val fileFolder = "/file/example"

}
