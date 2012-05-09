package models

import org.squeryl.PrimitiveTypeMode._
import db.TodoSchema
import api.sorting.OrderDirection
import org.squeryl.dsl.ast.ExpressionNode
import org.squeryl.KeyedEntity
import org.joda.time.DateTime

case class Todo private (
  id: Long,
  text: String,
  due: DateTime,
  done: Boolean) extends KeyedEntity[Long]

object Todo {
  import TodoSchema._

  object Order extends Enumeration {
    val id = Value("id")
    val text = Value("text")
    val done = Value("done")

    def buildOrder(todo: Todo, order: Order.Value, od: OrderDirection.Value): ExpressionNode = order match {
      // We use backticks to avoid declaring a new variable
      case `id` => if (od == OrderDirection.asc) todo.id else todo.id desc
      case `text` => if (od == OrderDirection.asc) todo.text else todo.text desc
      case `done` => if (od == OrderDirection.asc) todo.id else todo.done desc
    }
  }

  def apply(text: String, due: DateTime, done: Boolean) = {
    new Todo(0, text, due, done)
  }
  def findById(id: Long): Option[Todo] = inTransaction {
    from(todosTable)(t => where(t.id === id) select (t)).headOption
  }

  def find(search: Option[String], withDone: Boolean, order: Order.Value, direction: OrderDirection.Value): Seq[Todo] = inTransaction {
    val doneFilter = if(withDone) None else Some(false)
    from(todosTable)((todo) =>
      where(
          todo.text like search.? and
          todo.done === doneFilter.?
      ) 
        select (todo)
        orderBy Order.buildOrder(todo, order, direction)).toList
  }

  def insert(todo: Todo): Todo = inTransaction {
    todosTable.insert(todo.copy())
  }
  
  def delete(id: Long) = inTransaction {
    todosTable.delete(id)
  }
}