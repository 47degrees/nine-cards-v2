package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.{File, FileOutputStream, InputStream}

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import com.fortysevendeg.ninecardslauncher.services.image.{BitmapTransformationException, FileException}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ImageServicesTasksSpecification
  extends Specification
  with Mockito {

  trait ImageServicesTasksScope
    extends Scope
      with ImageServicesImplData {

    class ImageServicesTaskImpl extends ImageServicesTasks

    val contextSupport = mock[ContextSupport]
    val packageManager = mock[PackageManager]

    val mockImageServicesTask = new ImageServicesTaskImpl

    val mockResources = mock[Resources]
    val mockFile = mock[File]
    val mockBitmap = mock[Bitmap]

    contextSupport.getPackageManager returns packageManager

  }
}


class ImageServicesTasksSpec
  extends ImageServicesTasksSpecification {

  "Image Services Tasks" should {

    "return a File when a valid packageName and a valid className is provided" in
      new ImageServicesTasksScope {

        contextSupport.getResources returns mockResources

        val mockResourceUtils = new ResourceUtils {
          override def getPath(filename: String)(implicit context: ContextSupport): String = s"$fileFolder/$filename"
        }

        override val mockImageServicesTask = new ImageServicesTaskImpl {
          override val resourceUtils = mockResourceUtils
        }

        val result = mockImageServicesTask.getPathByName(packageName)(contextSupport).value.run
        result must beLike {
          case Right(resultFile) =>
            resultFile.getName shouldEqual packageName
            resultFile.getPath shouldEqual resultFilePathPackage
        }
      }

    "return a FileException when getPath in resourceUtils returns an empty string" in
      new ImageServicesTasksScope {

        val result = mockImageServicesTask.getPathByName(packageName)(contextSupport).value.run
        result must beAnInstanceOf[Left[FileException, _]]

      }

    "return a Bitmap when when a valid uri is provided" in
      new ImageServicesTasksScope {

        val mockInputStream = mock[InputStream]

        override val mockImageServicesTask = new ImageServicesTaskImpl {
          override def createInputStream(uri: String) = mockInputStream

          override def createBitmapByInputStream(is: InputStream) = mockBitmap
        }

        val result = mockImageServicesTask.getBitmapFromURL(uri).value.run
        result shouldEqual Right(mockBitmap)
      }

    "return a BitmapTransformationException with an invalid uri" in
      new ImageServicesTasksScope {

        override val mockImageServicesTask = new ImageServicesTaskImpl {
          override def createInputStream(uri: String) = javaNull
        }

        val result = mockImageServicesTask.getBitmapFromURL(uri).value.run
        result must beAnInstanceOf[Left[BitmapTransformationException, _]]
      }


    "successfuly saves the bitmap in the file" in
      new ImageServicesTasksScope {

        val mockFileOutputStream = mock[FileOutputStream]

        override val mockImageServicesTask = new ImageServicesTaskImpl {
          override def createFileOutputStream(file: File): FileOutputStream = mockFileOutputStream
        }

        val result = mockImageServicesTask.saveBitmap(mockFile, mockBitmap).value.run
        result shouldEqual Right((): Unit)
        there was one(mockBitmap).compress(Bitmap.CompressFormat.PNG, 90, mockFileOutputStream)
      }

    "return a FileException when the bitmap can not be saved" in
      new ImageServicesTasksScope {

        override val mockImageServicesTask = new ImageServicesTaskImpl {
          override def createFileOutputStream(file: File): FileOutputStream = javaNull
        }
        val result = mockImageServicesTask.saveBitmap(mockFile, mockBitmap).value.run
        result must beAnInstanceOf[Left[FileException, _]]
      }
  }
}
