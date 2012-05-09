package util.binding

import models.Todo
import api.sorting.OrderDirection

object binders {
  implicit object OrderDirectionBindable extends EnumerationBindable[OrderDirection.Value](OrderDirection)
  implicit object TodoOrderBindable extends EnumerationBindable[Todo.Order.Value](Todo.Order)
}