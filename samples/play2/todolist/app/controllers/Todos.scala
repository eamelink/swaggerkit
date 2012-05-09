package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.data.Form
import play.api.data.Form._
import play.api.data.Forms._
import net.eamelink.swaggerkit._
import net.eamelink.swaggerkit.SimpleTypes._
import models.Todo
import models.formats.DefaultTodo
import api.sorting.OrderDirection
import org.joda.time.DateTime

object Todos extends Controller {
  val dateFormat = "yyyy-MM-dd hh:mm";

  lazy val todosApi = Api("/todos") describedBy "A todos API" withOperations (listOperation, createOperation)
  lazy val todoApi = Api("/todos/{todoId}") describedBy "A todo API" withOperations (showOperation, updateOperation, deleteOperation)

  lazy val listOperation = Operation("listTodos", GET, "Get a list of todos") takes (
    QueryParam("search", String) is "A search query. Searches in the todo text.",
    QueryParam("withDone", Boolean) is "Show completed todos as well. Defaults to '0'",
    QueryParam("orderBy", String) is "The sort field. Defaults to 'due'" withValues Todo.Order,
    QueryParam("direction", String) is "The order direction. Defaults to 'asc'" withValues OrderDirection) note
    "This is just a sample note."

  def list(search: Option[String], withDone: Option[Boolean], orderBy: Option[Todo.Order.Value], direction: Option[OrderDirection.Value]) = Action {
    Ok(Json.toJson(Todo.find(search.map("%" + _ + "%"), withDone.getOrElse(false), orderBy.getOrElse(Todo.Order.id), direction.getOrElse(OrderDirection.asc))))
  }

  val showOperation = Operation("showTodo", GET, "Get a todo") takes (
    PathParam("todoId", String) is "The todo id") note
    "And this is another sample note."

  def show(todoId: Long) = Action { implicit request =>
    Todo.findById(todoId).map { todo =>
      Ok(Json.toJson(todo))
    }.getOrElse[Result](NotFound)
  }

  val createOperation = Operation("createTodo", POST, "Create a todo") takes (
    BodyParam(DefaultTodo.schema) is "The Todo item you want to create") note
    "This method returns the created todo item, and a Location header pointing to its url."

  def create() = Action { implicit request =>
    val createForm = Form(
      tuple(
        "text" -> text,
        "due" -> date(dateFormat),
        "done" -> boolean))
    createForm.bindFromRequest.fold(
      formWithErrors => Forbidden(formWithErrors.errorsAsJson),
      value => {
        val createdTodo = Todo.insert(
          Todo(value._1, new DateTime(value._2.getTime), value._3))
        Created(Json.toJson(createdTodo)).withHeaders(
          LOCATION -> routes.Todos.show(createdTodo.id).toString())
      })
  }

  val updateOperation = Operation("updateTodo", PUT, "Update a todo") takes (
    PathParam("todoId", String) is "The id of the todo to update",
    BodyParam(DefaultTodo.schema)) note
    "This operation returns the updated todo item, and a Location header pointing to its url."

  def update(todoId: Long) = Action { implicit request =>

    Todo.findById(todoId).map { existingTodo =>
      val updateForm = Form(
        tuple(
          "text" -> text,
          "done" -> boolean,
          "due" -> date(dateFormat)))

      val json = request.body.asJson.get.asInstanceOf[JsObject]

      updateForm.bindFromRequest.fold(
        formWithErrors => Forbidden(formWithErrors.errorsAsJson),
        value => {
          val updatedTodo = existingTodo.copy(
            text = value._1)
          Ok(Json.toJson(updatedTodo)).withHeaders(
          LOCATION -> routes.Todos.show(updatedTodo.id).toString())
        })
    }.getOrElse(NotFound)
  }

  lazy val deleteOperation = Operation("deleteTodo", DELETE, "Delete a todo item") takes (
    PathParam("todoId", String) is "The id of the todo to delete") note
    "This method returns a 204 when successful, a 404 when the todo item was not found."
    
  def delete(todoId: Long) = Action {
    Todo.findById(todoId).map{ todo => 
      Todo.delete(todo.id)
      NoContent
    }.getOrElse(NotFound)
  }
}