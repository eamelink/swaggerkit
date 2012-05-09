package controllers

import play.api.mvc._
import play.api.libs.json._
import net.eamelink.swaggerkit._
import net.eamelink.swaggerkit.play2.Writers._
object Swagger extends Controller {
  
  def apiDocumentation(host: String) = ApiDocumentation(
    basePath = "http://" + host + "/api",
    swaggerVersion = "1.1-SNAPSHOT",
    apiVersion = "1",
    apis = List(
      ResourceDeclaration(
        path = "/todos.{format}",
        description = "Operations on todo-items",
        resourcePath = "/todos",
        basePath = "http://"+host+"/api",
        swaggerVersion = "1.1-SNAPSHOT",
        apiVersion = "1",
        apis = List(
          Todos.todosApi,
          Todos.todoApi
        ),
        models = Map(
          "Todo" -> models.formats.DefaultTodo.schema))))
  
  def ui() = Action {
    Redirect("/assets/swagger-ui/index.html")
  }
  
  def discover() = Action { request =>
    Ok(Json.toJson(apiDocumentation(request.host)))
  }
  
  def resource(id: String) = Action { request => 
    val path = "/%s.{format}" format id
    apiDocumentation(request.host).apis.filter(_.path == path).headOption.map { doc =>
      Ok(Json.toJson(doc))
    }.getOrElse(NotFound)
  }
}
