package Repository

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import Controller.ClientsController
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Server extends App with ClientsController  {
  implicit val actorSystem = ActorSystem("AkkaHTTPExampleServices")
  implicit val materializer = ActorMaterializer()

  lazy val apiRoutes: Route = pathPrefix("api") {
    ClientRoutes
  }
  Http().bindAndHandle(apiRoutes, "localhost", 8081)
  Await.result(actorSystem.whenTerminated, Duration.Inf)


}
