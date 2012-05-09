package net.eamelink.swaggerkit.play2

import net.eamelink.swaggerkit._
import play.api.libs.json._
import scala.collection.mutable.ListBuffer

object Writers {

  implicit object ApiDocumentationWriter extends Writes[ApiDocumentation] {
    def writes(rl: ApiDocumentation): JsValue = JsObject(List(
      "basePath" -> JsString(rl.basePath),
      "swaggerVersion" -> JsString(rl.swaggerVersion),
      "apiVersion" -> JsString(rl.apiVersion),
      "apis" -> JsArray(rl.apis.map(resourceDeclaration => JsObject(List(
        "path" -> JsString(resourceDeclaration.path),
        "description" -> JsString(resourceDeclaration.description)))).toList)))
  }

  implicit object ResourceDeclarationWriter extends Writes[ResourceDeclaration] {
    def writes(ad: ResourceDeclaration): JsValue = JsObject(List(
      "resourcePath" -> JsString(ad.resourcePath),
      "basePath" -> JsString(ad.basePath),
      "apiVersion" -> JsString(ad.apiVersion),
      "swaggerVersion" -> JsString(ad.swaggerVersion),
      "apis" -> JsArray(ad.apis.map(api => ApiWriter.writes(api))),
      "models" -> JsObject(ad.models.map(pair => pair._1 -> SchemaWriter.writes(pair._2)).toList)))
  }

  object ParameterWriter extends Writes[Parameter] {

    def writes(param: Parameter): JsValue = {
      var properties = new ListBuffer[(String, JsValue)]

      param.name.map(n => properties += "name" -> JsString(n))
      param.description.map(d => properties += "description" -> JsString(d))

      // A boolean is represented by type string, and a list of allowable values
      param.dataType.name match {
        case "boolean" => {
          properties += "dataType" -> JsString("string")
          properties += booleanValues
        }
        case _ => {
          properties += "dataType" -> JsString(param.dataType.name)
        }
      }

      properties += "required" -> JsBoolean(param.required)
      properties += "allowMultiple" -> JsBoolean(param.allowMultiple)
      properties += "paramType" -> JsString(param.paramType)
      if (param.allowableValues.isDefined) {
        properties += "allowableValues" -> JsObject(List(
          "values" -> JsArray(param.allowableValues.get.map(JsString(_)).toList),
          "valueType" -> JsString("LIST")))
      }
      param.valueTypeInternal.map(vti => properties += "valueTypeInternal" -> JsString(vti))

      JsObject(properties)
    }

    private val booleanValues = "allowableValues" -> JsObject(List(
      "values" -> JsArray(List(JsString("0"), JsString("1"))),
      "valueType" -> JsString("LIST")))
  }

  object OperationWriter extends Writes[Operation] {
    def writes(op: Operation): JsValue = JsObject(List(
      "parameters" -> JsArray(op.parameters.map(param => ParameterWriter.writes(param))),
      "httpMethod" -> JsString(op.httpMethod.toString),
      "notes" -> JsString(op.notes.getOrElse("")),
      "responseTypeInternal" -> JsString(op.responseTypeInternal.getOrElse("")),
      "nickname" -> JsString(op.nickName),
      "responseClass" -> JsString(op.responseClass.getOrElse("")),
      "summary" -> JsString(op.summary)))
  }

  object ApiWriter extends Writes[Api] {
    def writes(api: Api): JsValue = JsObject(List(
      "path" -> JsString(api.path),
      "description" -> JsString(api.description.getOrElse("")),
      "operations" -> JsArray(api.operations.map(op => OperationWriter.writes(op)))))
  }

  object PropertyWriter extends Writes[Property] {
    def writes(p: Property): JsValue = JsObject(List(
      "type" -> JsString(p.typ.name)))
  }

  object SchemaWriter extends Writes[Schema] {
    def writes(s: Schema): JsValue = JsObject(List(
      "properties" -> {
        s.properties match {
          case None => JsNull
          case Some(properties) => JsObject(properties.map(p => p._1 -> PropertyWriter.writes(p._2)).toList)
        }
      }))
  }
}
