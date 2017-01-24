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

package cards.nine.services.apps.impl

import android.content.Intent
import android.content.pm._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.models.ApplicationData
import cards.nine.services.apps.AppsInstalledException
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.collection.JavaConversions._

trait AppsServicesImplSpecification extends Specification with Mockito {

  trait AppsServicesImplScope extends Scope with AppsServicesImplData {

    val packageManager = mock[PackageManager]
    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns packageManager

    val mockIntent = mock[Intent]

    def createMockResolveInfo(sampleApp: ApplicationData): ResolveInfo = {
      val sampleResolveInfo   = mock[ResolveInfo]
      val mockActivityInfo    = mock[ActivityInfo]
      val mockApplicationInfo = mock[ApplicationInfo]
      sampleResolveInfo.activityInfo = mockActivityInfo
      mockActivityInfo.name = sampleApp.className
      mockActivityInfo.applicationInfo = mockApplicationInfo
      mockApplicationInfo.packageName = sampleApp.packageName
      sampleResolveInfo.loadLabel(packageManager) returns sampleApp.name
      sampleResolveInfo
    }

    def createMockPackageInfo(sampleApp: ApplicationData): PackageInfo = {
      val samplePackageInfo   = mock[PackageInfo]
      val mockApplicationInfo = mock[ApplicationInfo]
      samplePackageInfo.applicationInfo = mockApplicationInfo
      samplePackageInfo.packageName = sampleApp.packageName
      mockApplicationInfo.name = sampleApp.name
      mockApplicationInfo.className = sampleApp.className
      samplePackageInfo.firstInstallTime = sampleApp.dateInstalled
      samplePackageInfo.lastUpdateTime = sampleApp.dateUpdated
      samplePackageInfo.versionCode = sampleApp.version.toInt
      samplePackageInfo
    }

    val mockApps = List(createMockResolveInfo(sampleApp1), createMockResolveInfo(sampleApp2))

    val packageInfo1 = createMockPackageInfo(sampleApp1)
    val packageInfo2 = createMockPackageInfo(sampleApp2)

    packageManager.getPackageInfo(sampleApp1.packageName, 0) returns packageInfo1
    packageManager.getPackageInfo(sampleApp2.packageName, 0) returns packageInfo2
    packageManager.getInstallerPackageName(sampleApp1.packageName) returns androidFeedback
    packageManager.getInstallerPackageName(sampleApp2.packageName) returns androidFeedback

    val mockAppsServicesImpl = new AppsServicesImpl

    val exception = AppsInstalledException("")

  }

}

class AppsServicesImplSpec extends AppsServicesImplSpecification {

  "Apps Services" should {

    "getInstalledApplications" should {

      "returns the list of installed apps when they exist" in
        new AppsServicesImplScope {

          packageManager.queryIntentActivities(any, any) returns mockApps
          val result = mockAppsServicesImpl.getInstalledApplications(contextSupport).value.run
          result shouldEqual Right(applicationList)
        }

      "returns an AppsInstalledException when no apps exist" in
        new AppsServicesImplScope {

          packageManager.queryIntentActivities(any, any) throws exception
          val result = mockAppsServicesImpl.getInstalledApplications(contextSupport).value.run
          result must beAnInstanceOf[Left[AppsInstalledException, _]]
        }
    }

    "getApplication" should {

      "returns the installed app when a valid packageName is provided" in
        new AppsServicesImplScope {

          packageManager.resolveActivity(mockIntent, 0) returns mockApps.head
          packageManager.getLaunchIntentForPackage(sampleApp1.packageName) returns mockIntent

          val result =
            mockAppsServicesImpl.getApplication(validPackageName)(contextSupport).value.run
          result shouldEqual Right(sampleApp1)
        }

      "returns an AppsInstalledException when an invalid packageName is provided" in
        new AppsServicesImplScope {

          packageManager.getLaunchIntentForPackage(invalidPackageName) throws exception
          val result =
            mockAppsServicesImpl.getApplication(invalidPackageName)(contextSupport).value.run
          result must beAnInstanceOf[Left[AppsInstalledException, _]]
        }

      "returns an AppsInstalledException when the resolveActivity method fails" in
        new AppsServicesImplScope {

          packageManager.resolveActivity(mockIntent, 0) throws exception
          val result =
            mockAppsServicesImpl.getApplication(validPackageName)(contextSupport).value.run
          result must beAnInstanceOf[Left[AppsInstalledException, _]]
        }

      "returns an AppsInstalledException when the getPackageInfo method fails" in
        new AppsServicesImplScope {

          packageManager.getPackageInfo(sampleApp1.packageName, 0) throws exception
          val result =
            mockAppsServicesImpl.getApplication(validPackageName)(contextSupport).value.run
          result must beAnInstanceOf[Left[AppsInstalledException, _]]
        }
    }

    "getDefaultApps" should {

      "returns the list of installed default apps when they exist" in
        new AppsServicesImplScope {

          packageManager.queryIntentActivities(any, any) returns mockApps
          val result = mockAppsServicesImpl.getDefaultApps(contextSupport).value.run
          result shouldEqual Right(defaultApplicationList)
        }

      "returns an AppsInstalledException when no default apps exist" in
        new AppsServicesImplScope {

          packageManager.queryIntentActivities(any, any) throws exception
          val result = mockAppsServicesImpl.getDefaultApps(contextSupport).value.run
          result must beAnInstanceOf[Left[AppsInstalledException, _]]
        }
    }
  }

}
