package Controller

import akka.actor.ActorSystem
import akka.event.Logging
import Repository.ClientRepo.Client
import Repository.ClientRepo
import Controller.ClientsController.QueryClient
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import javafx.util.Duration.seconds
import spray.json.DefaultJsonProtocol.listFormat

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
//import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.pathPrefix
///import aClientController.{UpdateFeed, UpdateName}
//import Controller.ClientsController.Q
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol

import scala.util.{Failure, Success}
case class UpdateClient(id: String,
                        firstName: String,
                       )
object ClientsController {
  case class QueryClient(id: String,
                         firstName: String,
                         lastName: String)

  object ClientJsonProtocol
  extends SprayJsonSupport
  with DefaultJsonProtocol {

    //implicit val addressFormat = jsonFormat(Address.apply)
    implicit val ClientFormat1 = jsonFormat2(UpdateClient.apply)
    implicit val ClientFormat = jsonFormat4(Client.apply)
    implicit val ClientQueryFormat = jsonFormat3(QueryClient.apply)
  }
}
trait  ClientsController extends ClientRepo {
  implicit def actorSystem: ActorSystem

  lazy val logger = Logging(actorSystem, classOf[ClientsController])

  import Controller.ClientsController.ClientJsonProtocol._
  import ClientRepo._

  val cdirective = pathPrefix("users") | pathPrefix("Client")
  implicit val materializer: ActorMaterializer
  lazy val ClientRoutes: Route = cdirective {
    /*  get {
      println("here")
      path(Segment) { id =>
        onComplete(getClientId(id)) {
          _ match {
            case Success(client) =>
              logger.info(s"Got the CLient records given the client id ${id}")
              complete(StatusCodes.OK,client)
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
          onComplete(getUsers()){
            case Success(value) => complete(StatusCodes.OK)

          }

        }
      }
    } ~ */
    post {
      println("here")
      path("create") {
        entity(as[QueryClient]) { q =>
          pathEndOrSingleSlash {
            onComplete(createUser(q.id, q.firstName, q.lastName)) {
              _ match {
                case Success(q) =>
                  complete(StatusCodes.OK, "done")
                case Failure(throwable) =>
                  complete(StatusCodes.InternalServerError, "Failed to query the Client")
              }
            }
          }
        }
      }
    } ~ delete {
      pathPrefix("delete") {
        path(Segment) { name =>
          println("IN Delete")
          onComplete(deleteUser(name)) {
            case Success(value) => complete(StatusCodes.OK, "done")
            case Failure(exception) => complete(StatusCodes.InternalServerError, "Failed to delete the client")
          }
        }
      }
    } ~ put {
      println("here")
      path("update") {
        entity(as[UpdateClient]) { q =>
          pathEndOrSingleSlash {
            onComplete(UpdateUser(q.id, q.firstName)) {
              _ match {
                case Success(q) =>
                  complete(StatusCodes.OK, "done")
                case Failure(throwable) =>
                  complete(StatusCodes.InternalServerError, "Failed to query the Client")
              }
            }
          }
        }
      }
    } ~ get {
      println("here")
      path(Segment) { id =>
        onComplete(getClientId(id)) {
          _ match {
            case Success(client) =>
              logger.info(s"Got the CLient records given the client id ${id}")
              complete(StatusCodes.OK, client)
            case Failure(throwable) =>
              throwable match {
                case e: ClientNotFoundException => complete(StatusCodes.NotFound, "Mo Client Found")
                case e: DubiousClientRecordException => complete(StatusCodes.NotFound, "Dubious records found")
                case _ => complete(StatusCodes.InternalServerError, "Failed to get the Client")
              }
          }
        }
      }~ pathPrefix("search") {
        path("all") {
          onComplete(getUsers()) {
            case Success(value) => complete(StatusCodes.OK,value.head)

          }
        }
      }
    }
  }
}
  /*val userRoutes :Route ={
    pathPrefix("client"){
      concat(
        pathEnd{
          concat(
            /*get {
              complete(getUsers())
            },*/
            post{
              entity(as[Client]){client =>
                onSuccess(createUser(client)){
                  complete(StatusCodes.Created,"Done")
                }
              }
            }
          )
        },
        path(Segment){name =>
          delete{
            println("IN Delete")
            onSuccess(deleteUser(name)){performed =>
              complete(StatusCodes.OK,performed)
            }
          }
        }
      )
    }
  }*/

