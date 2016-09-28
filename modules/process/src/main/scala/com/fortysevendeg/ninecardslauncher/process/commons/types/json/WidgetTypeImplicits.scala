package cards.nine.process.commons.types.json

import cards.nine.process.commons.types.WidgetType
import play.api.libs.json._

object WidgetTypeImplicits {

  implicit val widgetTypeReads = new Reads[WidgetType] {
    def reads(js: JsValue): JsResult[WidgetType] = {
      JsSuccess(WidgetType(js.as[String]))
    }
  }

  implicit val widgetTypeWrites = new Writes[WidgetType] {
    def writes(widgetType: WidgetType): JsValue = {
      JsString(widgetType.name)
    }
  }

}
