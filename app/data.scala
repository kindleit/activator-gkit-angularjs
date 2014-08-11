package app

import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime

case class Todo
  (
    _id             : BSONObjectID
  , description     : String
  , completed       : Boolean
  , createdAt       : DateTime
  )

case class NewTodo
  (
    description     : String
  )

case class UpdateTodo
  (
    description     : Option[String]
  , completed       : Option[Boolean]
  , createdAt       : DateTime = DateTime.now  
  )

case class FindParams
  (
    sortby          : Option[String]
  , asc             : Option[Boolean]
  )
