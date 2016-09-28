package com.fortysevendeg.ninecardslauncher.services.utils

import java.io.File

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ResourceUtilsSpecification
  extends Specification
    with Mockito {

  trait ResourceUtilsScope
    extends Scope
      with ResourceUtilsData {

    val mockContextSupport = mock[ContextSupport]
    val resourceUtils = new ResourceUtils
    val mockFile = mock[File]
  }
}

class ResourceUtilsSpec
  extends ResourceUtilsSpecification {

  "Resource Utils" should {

    "return the file path when a valid file name is provided" in
      new ResourceUtilsScope {

        mockContextSupport.getAppIconsDir returns mockFile
        mockFile.getPath returns fileFolder

        val result = resourceUtils.getPath(fileName)(mockContextSupport)
        result shouldEqual resultFilePath
      }
  }
}
