package db

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import models.Todo

object TodoSchema extends Schema {
  val todosTable = table[Todo]
  
  on(todosTable) (t => declare(
    t.id is(primaryKey, autoIncremented)    
  ))
}