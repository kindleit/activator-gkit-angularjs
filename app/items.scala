package app

import gkit.mongo._

import play.api.mvc.Result

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.modules.gresource.Http
import play.modules.gresource.Http._
import play.modules.gresource.mongo._

import reactivemongo.bson.BSONObjectID

import scalaz._
import scalaz.Scalaz._

import shapeless._

object Todos extends TodoFunctions {

  lazy val httpApi =
    Http.get(Prefix)(httpFind) |:
    Http.get(Prefix /? "id")(httpFindOne("id")) |:
    Http.jsonPost(Prefix)(httpInsert) |:
    Http.jsonPut(Prefix /? "id")(httpUpdate("id")) |:
    Http.delete(Prefix /? "id")(httpDelete("id"))
}

trait TodoFunctions extends QueryFunctions {

  val cname = "todos"

  val idLens = lens[Todo] >> '_id

  def httpFind: Kleisli[EFE, AReq, Result] =
    paramsFromReq[FindParams].apply >>>
    liftK(mkFindQry _ >>> find[Todo](cname)) >>>
    toJsonResult

  def httpFindOne(pname: String): Kleisli[EFE, AReq, Result] =
    pathParam[BSONObjectID](pname) >>>
    liftK(findOneById[Todo](cname)) >>>
    toJsonResult

  def httpInsert: Kleisli[EFE, JReq, Result] =
    jsonFromReq[NewTodo] >>>
    liftK(mkNewTodo _ >>> insert[Todo](cname)(idLens)) >>>
    toJsonResult

  def httpUpdate(pname: String): Kleisli[EFE, JReq, Result] =
    (pathParam[BSONObjectID](pname) &&& jsonFromReq[UpdateTodo]) >>>
    liftK((mkUpdateQry _).tupled >>> update(cname)) >>>
    okResult

  def httpDelete(pname: String): Kleisli[EFE, AReq, Result] =
    pathParam[BSONObjectID](pname) >>>
    liftK(deleteById(cname)) >>>
    okResult

  def mkFindQry(fp: FindParams) =
    FindQuery(
      query    = EmptyQ,
      proj     = EmptyQ,
      defaults = fp)

  def mkNewTodo(ni: NewTodo): Todo =
    Todo(
      _id         = BSONObjectID.generate,
      description = ni.description,
      completed   = false)

  def mkUpdateQry(id: BSONObjectID, ui: UpdateTodo) =
    UpdateQuery(
      query    = IdQ(id),
      modifier = Set(ui))
}
