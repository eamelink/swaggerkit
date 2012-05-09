package net.eamelink.swaggerkit

/**
 * Container for the entire API documentation.
 *
 * This is a model for the resource discovery page.
 */
case class ApiDocumentation(
  basePath: String,
  swaggerVersion: String,
  apiVersion: String,
  apis: List[ResourceDeclaration])

/**
 * Resource declaration, lists api's and model's for a given resource.
 */
case class ResourceDeclaration(
  // Properties shown on the resource discovery page.
  path: String,
  description: String,

  // Properties shown on the page for the given resource.
  resourcePath: String,
  basePath: String,
  swaggerVersion: String,
  apiVersion: String,
  apis: List[Api],
  models: Map[String, Schema])

object ResourceDeclaration {
  /**
   * Resource declaration constructor that finds all models in operations.
   */
  def apply(path: String, description: String, resourcePath: String, basePath: String, swaggerVersion: String, apiVersion: String, apis: List[Api]): ResourceDeclaration =
    ResourceDeclaration(path, description, resourcePath, basePath, swaggerVersion, apiVersion, apis, findModels(apis))

  /**
   * Find models that are references by the list of Api.
   *
   * Currently only looks for parameters, not operation return types.
   */
  private def findModels(apis: List[Api]): Map[String, Schema] = {
    apis.flatMap { api =>
      api.operations.flatMap { operation =>
        operation.parameters.map(_.dataType).collect {
          case schema: Schema => (schema.name -> schema)
        }
      }
    }.toMap
  }
}

/**
 * API, collection of operations on the same URL
 */
case class Api(
  path: String,
  description: Option[String] = None,
  operations: List[Operation] = Nil) {

  def describedBy(description: String) = copy(description = Some(description))
  def withOperations(ops: Operation*) = copy(operations = ops.toList)
}

/**
 * Http operation on a given url
 */
case class Operation(
  nickName: String,
  httpMethod: HttpMethod,
  summary: String,
  parameters: List[Parameter] = Nil,
  notes: Option[String] = None,
  responseTypeInternal: Option[String] = None,
  responseClass: Option[String] = None) {

  def takes(params: Parameter*) = copy(parameters = params.toList)
  def note(note: String) = copy(notes = Some(note))
}

/**
 * Http request parameter
 */
case class Parameter(
  name: Option[String],
  description: Option[String] = None,
  dataType: Type,
  required: Boolean = false,
  valueTypeInternal: Option[String] = None,
  allowMultiple: Boolean = false,
  allowableValues: Option[Seq[String]] = None,
  paramType: String) {

  def is(description: String) = copy(description = Some(description))
  def isRequired() = copy(required = true)
  def isOptional() = copy(required = false)
  def allowsMultiple() = copy(allowMultiple = true)
  def noMultiple() = copy(allowMultiple = false)
  def withValues(values: String*) = copy(allowableValues = Some(values))
  def withValues(enum: Enumeration) = copy(allowableValues = Some(enum.values.map(_.toString).toList))
}

/**
 * Factory for 'query' request parameters
 */
object QueryParam {
  def apply(name: String, dataType: Type) = Parameter(name = Some(name), dataType = dataType, paramType = "query", allowableValues = None)
}

/**
 * Factory for 'path' request parameters
 */
object PathParam {
  def apply(name: String, dataType: Type) = Parameter(name = Some(name), dataType = dataType, paramType = "path", required = true, allowableValues = None)
}

/**
 * Factory for 'body' request parameters
 */
object BodyParam {
  def apply(dataType: Type) = Parameter(None, dataType = dataType, paramType = "body", required = true)
}

/**
 * Http methods: GET, POST etc.
 */
sealed trait HttpMethod
case object GET extends HttpMethod
case object POST extends HttpMethod
case object PUT extends HttpMethod
case object DELETE extends HttpMethod

/**
 * A description of a complex type.
 */
case class Schema(name: String, properties: Option[Map[String, Property]] = None) extends Type {
  def has(props: (String, Property)*) = copy(properties = Some(props.toMap))
}

/**
 * A property of a Schema.
 */
case class Property(
  typ: Type,
  description: Option[String] = None,
  allowableValues: Option[Seq[String]] = None) {

  def is(description: String) = copy(description = Some(description))
  def allows(vals: String*) = copy(allowableValues = Some(vals.toSeq))
}

/**
 * A property type from a 'model' in the Swagger output.
 *
 * This can be a Simple type, or a Schema. In Swagger, for a Schema just the name is outputted.
 */
trait Type {
  def name: String
}

/**
 * Predefined primitives, or "Simple Types" as the spec calls them
 *
 * @see http://tools.ietf.org/html/draft-zyp-json-schema-03 section 5.1
 */
object SimpleTypes {
  object String extends Type { val name = "string" }
  object Number extends Type { val name = "number" }
  object Integer extends Type { val name = "integer" }
  object Boolean extends Type { val name = "boolean" }
  object Object extends Type { val name = "object" }
  object Any extends Type { val name = "any" }
  case class Array(innerType: Type) extends Type { def name = "array" }
}