package AkkaHTTP
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.{ContentType, ContentTypes, HttpEntity, HttpMethod, HttpMethods, HttpResponse, StatusCodes}
import akka.actor.ActorSystem
//import akka.actor.Status.{Failure, Success}
import akka.http.scaladsl.Http
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.concurrent.{Await, Future}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
//import com.typesafe.scalalogging.slf4j.LazyLogging

import spray.json.DefaultJsonProtocol
import spray.json._
import com.fasterxml.jackson.core.PrettyPrinter

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
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
 /// val serverVersion1 = new AkkaHTTP.ServerVersion()
  val serverVersionRouteAsJson = serverVersion.route()
  val serverVersionJsonEncoding = serverVersion.routeAsJsonEncoding()
  val donutRoutes = new DonutRoutes().route()
  val routes: Route =  donutRoutes ~ serverVersionRoute ~ serverVersionRouteAsJson ~ serverVersionJsonEncoding~
    serverUpRoute

  //val routes: Route =  serverVersionRoute ~ serverVersionRouteAsJson ~ serverVersionJsonEncoding ~ serverUpRoute

  val httpServerFuture = Http().bindAndHandle(routes, host, port)

  /*StdIn.readLine() // let it run until user presses return
  httpServerFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done*/
}




class ServerVersion extends JsonSupport {
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
        complete(server)
      }
    }
  }
}

class DonutRoutes extends JsonSupport {
  val donutDao = new DonutDao()

  def route(): Route = {
    path("create-donut") {
      post {
        entity(as[Donut]) { donut =>
          // logger.info(s"creating donut = $donut")
          complete(StatusCodes.Created, s"Created donut = $donut")
        }
      } ~ delete {
        complete(StatusCodes.MethodNotAllowed, "The HTTP DELETE operation is not allowed for the create-donut path.")
      }
    } ~ path("donuts") {
      get {
        onSuccess(donutDao.fetchDonuts()) { donuts =>
          complete(StatusCodes.OK, donuts)
        }
      }
    } ~ path("donuts-with-future-success-failure") {
      get {
        onComplete(donutDao.fetchDonuts()) {
          case Success(donuts) => complete(StatusCodes.OK, donuts)
          case Failure(exception: Exception) => complete("Failed to fetch donuts")
        }
      }
    }~ path("complete-with-http-response") {
      get {
        complete(HttpResponse(status = StatusCodes.Created, entity = "Using an HttpResponse object"))
      }
    }~ path("donut-with-try-httpresponse") {
      get {
        val result: HttpResponse = donutDao.tryFetchDonuts().getOrElse(donutDao.defaultResponse())
        complete(result)
      }
    }
  }
}

class DonutDao {

  val donutsFromDb = Vector(
    Donut("Plain Donut", 1.50),
    Donut("Chocolate Donut", 2),
    Donut("Glazed Donut", 2.50)
  )

  def fetchDonuts(): Future[Donuts] = Future {
    Donuts(donutsFromDb)
    }
    def tryFetchDonuts(): Try[HttpResponse] = Try {
      throw new IllegalStateException("Boom!")
    }

    def defaultResponse(): HttpResponse =
      HttpResponse(
        status = StatusCodes.NotFound,
        entity = "An unexpected error occurred. Please try again.")

}
//final case class AkkaHTTP.AkkaHttpRestServer(app: String, version: String)

