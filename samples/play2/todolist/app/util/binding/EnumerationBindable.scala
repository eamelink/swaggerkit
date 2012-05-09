package util.binding

import play.api.mvc.QueryStringBindable

class EnumerationBindable[A](enum: Enumeration) extends QueryStringBindable[A] {
  override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, A]] = {
    params.get(key).map {
      passedValueSeq => enum.values.find { value => 
        value.toString == passedValueSeq.mkString
      }.map { 
        value => Right(value.asInstanceOf[A])
      }.getOrElse {
        Left("Invalid " + key + ", allowed are one of " + enum.values.mkString(", "))
      }
    }
  }
  
  override def unbind(key: String, value: A): String = key + "=" + value 
}

