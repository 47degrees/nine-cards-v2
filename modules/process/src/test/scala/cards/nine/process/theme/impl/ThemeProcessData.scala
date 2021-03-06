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

package cards.nine.process.theme.impl

import android.graphics.Color
import cards.nine.models.types.theme.ThemeLight

trait ThemeProcessData {

  val nonExistingFileName        = "nonExistingFile.json"
  val defaultThemeName           = "theme name"
  val themeParentLight           = ThemeLight
  val themeParentLightName       = "light"
  val sampleColorWithAlpha       = "#ff59afdd"
  val sampleColorWithoutAlpha    = "#ffffff"
  val intSampleColorWithAlpha    = Color.parseColor(sampleColorWithAlpha)
  val intSampleColorWithoutAlpha = Color.parseColor(sampleColorWithoutAlpha)

  val validThemeJson =
    s"""
      |{
      |  "name": "$defaultThemeName",
      |  "parent": "$themeParentLightName",
      |  "styles": [
      |    {
      |      "styleType": "PrimaryColor",
      |      "color": "$sampleColorWithAlpha"
      |    },
      |    {
      |      "styleType": "DrawerTextColor",
      |      "color": "$sampleColorWithoutAlpha"
      |    }
      |  ],
      |  "themeColors": {
      |    "defaultColor": "$sampleColorWithAlpha",
      |    "colors": ["$sampleColorWithAlpha", "$sampleColorWithoutAlpha"]
      |  }
      |}
    """.stripMargin

  val wrongThemeParentJson =
    s"""
       |{
       |  "name": "$defaultThemeName",
       |  "parent": "unknownParent",
       |  "styles": [
       |    {
       |      "styleType": "PrimaryColor",
       |      "color": "#3F51B5"
       |    }
       |  ]
       |}
    """.stripMargin

  val wrongThemeStyleTypeJson =
    """
      |{
      |  "name": "light",
      |  "parent": "$themeParentLightName",
      |  "styles": [
      |    {
      |      "styleType": "UnknowStyleType",
      |      "color": "#ffffff"
      |    }
      |  ]
      |}
    """.stripMargin

  val wrongThemeStyleColorJson =
    """
      |{
      |  "name": "light",
      |  "parent": "$themeParentLightName",
      |  "styles": [
      |    {
      |      "styleType": "PrimaryColor",
      |      "color": "#ffff"
      |    }
      |  ]
      |}
    """.stripMargin

  val wrongThemeJson =
    """
      |{
      |  "name": "light"
      |}
    """.stripMargin

}
