package app

import reactivemongo.bson.BSONObjectID

case class Todo
  (
    _id             : BSONObjectID
  , description     : String
  , completed       : Boolean
  )

case class NewTodo
  (
    description     : String
  )

case class UpdateTodo
  (
    description     : Option[String]
  , completed       : Option[Boolean]
  )

case class FindParams
  (
    sortby          : Option[String]
  , asc             : Option[Boolean]
  )
