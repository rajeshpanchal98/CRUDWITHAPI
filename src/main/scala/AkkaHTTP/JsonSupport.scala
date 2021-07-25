//package akkaHttpServer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol.{jsonFormat1, jsonFormat2}
final case class AkkaHttpRestServer(app: String, version: String)
final case class Donut(name: String, price: Double)
final case class Donuts(donuts: Seq[Donut])

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  import spray.json._
  implicit val printer = PrettyPrinter

  implicit val serverFormat = jsonFormat2(AkkaHttpRestServer)
  implicit val donutFormat = jsonFormat2(Donut)
  implicit val donutsJsonFormat = jsonFormat1(Donuts)
}