package com.fortysevendeg.ninecardslauncher.services.permissions.impl

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.{ActivityContextSupport, ContextSupport}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.permissions._

class AndroidSupportPermissionsServices
  extends PermissionsServices
  with ImplicitsPermissionsServicesExceptions {

  override def checkPermissions(permissions: Seq[String])(implicit contextSupport: ContextSupport) =
    TaskService {
      CatchAll[PermissionsServicesException] {
        permissions.map { permission =>
          val status = ContextCompat.checkSelfPermission(contextSupport.context, permission) match {
            case PackageManager.PERMISSION_GRANTED => PermissionGranted
            case _ => PermissionDenied
          }
          permission -> status
        }.toMap
      }
    }

  override def shouldShowRequestPermissions(permissions: Seq[String])(implicit contextSupport: ActivityContextSupport) =
    TaskService {
      CatchAll[PermissionsServicesException] {
        withActivity { activity =>
          permissions.map { permission =>
            permission -> ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
          }.toMap
        }
      }
    }

  override def requestPermissions(requestCode: Int, permissions: Seq[String])(implicit contextSupport: ActivityContextSupport) =
    TaskService {
      CatchAll[PermissionsServicesException] {
        withActivity { activity =>
          ActivityCompat.requestPermissions(activity, permissions.toArray, requestCode)
        }
      }
    }

  def withActivity[T](f: (Activity) => T)(implicit contextSupport: ActivityContextSupport): T =
    contextSupport.getActivity match {
      case Some(activity) => f(activity)
      case None => throw new IllegalStateException("Activity not found in the context")
    }

  override def readPermissionsRequestResult(permissions: Seq[String], grantResults: Seq[Int]) =
    TaskService {
      CatchAll[PermissionsServicesException] {
        ((permissions zip grantResults) map {
          case (permission, PackageManager.PERMISSION_GRANTED) => permission -> PermissionGranted
          case (permission, _) => permission -> PermissionDenied
        }).toMap
      }
    }
}
