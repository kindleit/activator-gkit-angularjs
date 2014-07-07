package app

import gkit.mongo._

import play.api.Play.current

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.modules.gjson._
import play.modules.gresource.Http._
import play.modules.gresource.mongo._

import reactivemongo.bson.{BSONObjectID, BSONDocument}
import reactivemongo.core.commands.LastError
import reactivemongo.core.errors.DatabaseException

import scala.concurrent.Future

import scalaz._
import scalaz.Scalaz._

import shapeless._

trait QueryFunctions extends JSONPicklerInstances {

  case class FindQuery[A, B](query: A, proj: B, defaults: FindParams)

  case class UpdateQuery[A, B](query: A, modifier: B, upsert: Boolean = false, multi: Boolean = false)

  class Find[A] {
    def apply[B, C](cname: String)(fq: FindQuery[B, C])
      (implicit BP1: BSONPickler[A], BP2: BSONPickler[B], BP3: BSONPickler[C]): Future[Error \/ List[A]] = {
      val sortby = fq.defaults.sortby | "_id"
      val asc = fq.defaults.asc | true
      val sdoc = BSONDocument(sortby -> asc.fold(1, -1))
      val qb = dbe.collection(cname).find(fq.query, fq.proj).sort(sdoc)
      qb.cursor[A].collect[List]().map(_.leftMap(E500(_):Error))
    }
  }

  def find[A] = new Find[A]

  def findOneById[A](cname: String)(id: BSONObjectID)(implicit BP: BSONPickler[A]): Future[Error \/ Option[A]] =
    dbe.collection(cname).find(IdQ(id)).one[A].map(_.right[Error]).recover(onRecover)

  def insert[A](cname: String)(idLens: shapeless.Lens[A, BSONObjectID])(a: A)
    (implicit BP: BSONPickler[A]): Future[Error \/ IdQ[BSONObjectID]] =
    dbe.collection(cname).insert(a).map(errorOr(IdQ(idLens.get(a)))).recover(onRecover)

  def update[A, B](cname: String)(uq: UpdateQuery[A, B])
    (implicit BP1: BSONPickler[A], BP2: BSONPickler[B]): Future[Error \/ Unit] =
    dbe.collection(cname).update(uq.query, uq.modifier, uq.upsert, uq.multi).map(errorOr(())).recover(onRecover)

  def deleteById(cname: String)(id: BSONObjectID): Future[Error \/ Unit] =
    dbe.collection(cname).remove(IdQ(id)).map(errorOr(())).recover(onRecover)

  def dbe = GMongoPlugin.dbEnv

  private def onRecover[A]: PartialFunction[Throwable, Error \/ A] = {
    case de: DatabaseException =>
      E500(s"error: code: ${~de.code.map(_.toString)}: ${de.message}").left
    case e: Throwable =>
      E500(e.getMessage).left
  }

  private def errorOr[A](a: A)(le: LastError): Error \/ A =
    le.ok.fold(a.right, E500(~le.errMsg).left)
}
