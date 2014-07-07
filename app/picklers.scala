package app

import gkit.mongo._

import play.modules.gjson._

import play.api.libs.json._

import reactivemongo.bson.{BSONValue, BSONObjectID}

import scalaz._
import Scalaz._

trait JSONPicklerInstances {
  import org.joda.time.DateTime

  implicit def BSONObjectIDPickler: JSONPickler[BSONObjectID] =
    new JSONPickler[BSONObjectID] {
      def pickle(boid: BSONObjectID): JsValue = JsString(boid.stringify)

      def unpickle(v: JsValue, path: List[String]): String \/ BSONObjectID = {
        def parse(s: String) =
          try { BSONObjectID(s).right } catch { case e: Throwable => e.getMessage.left }

        for {
          js <- typecheck[JsString](v, path)(identity)
          id <- parse(js.value).leftMap(e => s"""error at: `${path.mkString(".")}`: $e""")
        } yield id
      }
    }

  implicit def DateTimeJSONPickler: JSONPickler[DateTime] =
    new JSONPickler[DateTime] {
      def pickle(dt: DateTime): JsValue = JsNumber(dt.getMillis)

      def unpickle(v: JsValue, path: List[String]): String \/ DateTime = for {
        js <- typecheck[JsNumber](v, path)(identity)
      } yield new DateTime(js.value.toLong)
    }

  implicit def BSONValuePickler: BSONPickler[BSONValue] = new BSONPickler[BSONValue] {
    def pickle(doc: BSONValue): BSONValue = doc
    def unpickle(v: BSONValue, path: List[String]): String \/ BSONValue =
      typecheck[BSONValue](v, path)(identity)
  }
}
