package Repository

import akka.util.Timeout
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import org.bson.codecs.configuration.CodecRegistries._
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.{Document, ObjectId}

import scala.language.postfixOps
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase, Observable, Observer}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.ExecutionContext.Implicits.global
//import Controller.ClientRepo.{ClientNotFoundException, Clients, DubiousClientRecordException, db}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
object ClientRepo  {
  case class Client(id: String,
                    firstName: String,
                    lastName: String,
                    active: Boolean
                   )

//  var ClientDB
//
//  = List(
//    Client("100", "Rajesh", "Panchal", true),
//    Client("101", "abc", "cdf", true),
//    Client("102", "bcd", "xyz", true),
//    Client("102", "cde", "pqr", true)
//
//
//  )
  val codecRegistry = fromRegistries(fromProviders(classOf[Client]), DEFAULT_CODEC_REGISTRY )
  val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017")
  var ClientDB: MongoDatabase = mongoClient.getDatabase("Mydb").withCodecRegistry(codecRegistry)
  val collection: MongoCollection[Client] = ClientDB.getCollection("test")
  val client: Client = Client("105","Rajesh","Panchal",true)

  class ClientNotFoundException extends Throwable("No Client found in db")

  class DubiousClientRecordException extends Throwable("Dubious Client records found given in the Client ID")
}
trait ClientRepo {
  import ClientRepo._
  def fetchDB():Future[List[Document]]= {
   // ClientDB
    val allclient : List[Client] = List.empty
    val res = Await.result(ClientDB.getCollection("test").find().toFuture(),10 seconds)
    var finallist :List[Document] = List.empty
    for(i <- res) {
      finallist = finallist :+ i
    }
      Future {
        finallist
      }
  }

  def getUsers():Future[List[Client]] = {
    val res = Await.result(collection.find().toFuture(),10 seconds)
    Future {
      res.toList
    }
  }
  def createUser(id:String,firstName:String,lastName:String):Future[List[Document]] = {
   val client :Client = Client(id,firstName,lastName,true)
   val res = collection.insertOne(client)
    Await.result(collection.insertOne(client).toFuture(), 10 seconds)
        val data = Await.result(ClientDB.getCollection("test").find().toFuture(), 10 seconds)
        var ClientDb: List[Document] = List.empty[Document]
        data.foreach(x =>
          ClientDb = ClientDb :+ x
        )
        Future {
          ClientDb
        }

  }
  def deleteUser(Id:String):Future[List[Client]] ={
    Await.result(collection.deleteOne(equal("id",Id)).toFuture(),10 seconds)
   getUsers()
  }
  def UpdateUser(Id:String,name :String):Future[List[Client]] ={
    Await.result(collection.updateOne(equal("id", Id), set("firstName", name)).toFuture(),10 seconds)
    getUsers()
  }
  def getClientId(iD:String) = fetchDB().map{db=>
    val data = Await.result(collection.find(equal("id",iD)).toFuture(),10 seconds)
    if(data.isEmpty)
      throw new ClientNotFoundException
    else if(data.length > 1)
      throw new DubiousClientRecordException
    else
      data.head
  }
  def queryClient(id:String,firstName:String,lastName:String) = {
    val data = Await.result(collection.find(equal("id",id)).toFuture(),10 seconds)
    if(data.isEmpty)
      throw new ClientNotFoundException
    else if(data.length > 1)
      throw new DubiousClientRecordException
    else
      data(0)

    }
  /*def fetchDB():Future[List[Client]]= {
    Future {
      ClientDB
    }
  }

    def getUsers():Future[List[Client]] = {
    Future{
              ClientDB
    }
  }


    def deleteUser(id:String):Future[List[Client]] ={
      implicit val timeout = new Timeout(50 seconds)
      val emp:Client = Await.result(getClientId(id),timeout.duration)
      println(emp)
      Future{
        ClientDB.filterNot(elm => elm == emp)
      }
    }

  def getClientId(id:String) = fetchDB().map{db=>
    val data = db.filter(_.id == id)
    if(data.isEmpty)
      throw new ClientNotFoundException
    else if(data.length > 1)
      throw new DubiousClientRecordException
      else
      data(0)
    }Await.result(collection.deleteOne(equal("name","rajes")).toFuture(),10 seconds)

    def queryClient(id:String,firstName:String,lastName:String) = {
      fetchDB().map { db=>
        val data = db.filter { elem =>
          elem.id == id && elem.firstName == firstName && elem.lastName == lastName
        }
        if(data.isEmpty)
          throw new ClientNotFoundException
        else if(data.length > 1)
          throw new DubiousClientRecordException
        else
          data(0)

      }
    }*/
}

