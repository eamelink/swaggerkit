package models

import play.api.libs.json._
import net.eamelink.swaggerkit._
import net.eamelink.swaggerkit.SimpleTypes._
import org.joda.time.format.ISODateTimeFormat

object formats {
  val dateTimeFormatter = ISODateTimeFormat.dateTime

  implicit object DefaultTodo extends Writes[Todo] with SchemaBuilder {
    def writes(todo: Todo): JsValue = JsObject(List(
      "id" -> JsNumber(todo.id),
      "text" -> JsString(todo.text),
      "due" -> JsString(dateTimeFormatter.print(todo.due)),
      "done" -> JsBoolean(todo.done)))

    lazy val schema = Schema("Todo") has (
      "id" -> Integer,
      "text" -> String,
      "due" -> String,
      "done" -> Boolean)
  }

}