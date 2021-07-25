import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol.jsonFormat2

import scala.util.Failure
import scala.util.Success
//akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import akka.http.scaladsl.model.{ContentType, ContentTypes, HttpEntity, HttpMethod, HttpMethods}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.io.StdIn

object AkkaHTTPServer  extends App {
  implicit val system = ActorSystem("akka-http-rest-server")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  val host = "127.0.0.1"
  val port = 8080

  val serverUpRoute: Route = get {
    complete("Akka HTTP Server is UP.")
  }

  val serverVersion = new ServerVersion()
  val serverVersionRoute = serverVersion.routeAsJson()
 /// val serverVersion1 = new ServerVersion()
  val serverVersionRouteAsJson = serverVersion.route()
  val serverVersionJsonEncoding = serverVersion.routeAsJsonEncoding()
  val routes: Route =  serverVersionRoute ~ serverVersionRouteAsJson ~ serverVersionJsonEncoding ~ serverUpRoute

  val httpServerFuture = Http().bindAndHandle(routes, host, port)

  /*StdIn.readLine() // let it run until user presses return
  httpServerFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done*/
}




class ServerVersion extends SprayJsonSupport {
  def routeAsJson(): Route = {
    path("server-version-json") {
      get {
        val jsonResponse =
          """
            |{
            | "app": "Akka HTTP REST Server",
            | "version": "1.0.0.0"
            |}
          """
            .stripMargin
        complete(HttpEntity(ContentTypes.`application/json`, jsonResponse))
      }
    }
  }

  def route(): Route = {
    path("server-version") {
      get {
        val serverVersion = "1.0.0.0"
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, serverVersion))
      }
    }
  }

  def routeAsJsonEncoding(): Route = {
    path("server-version-json-encoding") {
      get {
        val server = AkkaHttpRestServer("Akka HTTP REST Server", "1.0.0.0")
        complete( server)
      }
    }
  }
}
//final case class AkkaHttpRestServer(app: String, version: String)

