import akka.http.scaladsl.unmarshalling.{ FromEntityUnmarshaller, FromRequestUnmarshaller, Unmarshaller }
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.pathPrefix
///import aClientController.{UpdateFeed, UpdateName}
import org.mongodb.scala.bson.collection.mutable.Document
import spray.json.DefaultJsonProtocol.immSeqFormat
import ClientRepo.Client
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ClientsAPI.QueryClient
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import scala.util.{Failure, Success}

object ClientsAPI {
  case class QueryClient(id: String,
                         firstName: String,
                         lastName: String)

  object ClientJsonProtocol
  extends SprayJsonSupport
  with DefaultJsonProtocol {
    //implicit val addressFormat = jsonFormat(Address.apply)
    implicit val employeeFormat = jsonFormat4(Client.apply)
    implicit val ClientQueryFormat = jsonFormat3(QueryClient.apply)
  }
}
trait ClientController extends ClientRepo {
  implicit def actorSystem:ActorSystem
  lazy val logger = Logging(actorSystem,classOf[ClientController])
  import ClientsAPI.ClientJsonProtocol._
  import ClientRepo._
  val cdirective = pathPrefix("users") | pathPrefix("Client")
  lazy val ClientRoutes: Route = cdirective {
    get {
      println("here")
      path(Segment) { id =>
        onComplete(getClientId(id)) {
          _ match {
            case Success(client) =>
              complete(s"Got the CLient records given the client id ${id}")
            case Failure(throwable) =>
              throwable match {
                case e: ClientNotFoundException => complete(StatusCodes.NotFound, "Mo Client Found")
                case e: DubiousClientRecordException => complete(StatusCodes.NotFound, "Dubious records found")
                case _ => complete(StatusCodes.InternalServerError, "Failed to get the Client")
              }
          }
        }
      } ~ pathPrefix("search") {
        path("all") {

        }
      }
    } ~ post {
      pathPrefix("query") {
        entity(as[QueryClient]) { q =>
          pathEndOrSingleSlash {
            onComplete(queryClient(q.id, q.firstName, q.lastName)) {
              _ match {
                case Success(client) =>
                  complete (statusCodes: StatusCodes.OK, client)
                case Failure(throwable) =>
                  complete(StatusCodes.InternalServerError,"Failed to query the Client")
              }
            }
          }

        }
      }
    }
  }
}
