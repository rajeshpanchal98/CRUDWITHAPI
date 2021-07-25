import org.mongodb.scala.{MongoClient, Observer}
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates.set

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
object MongoConnector {
  val uri: String = "mongodb://localhost:27017"
  val dbName: String = "Mydb"
  def getCollection(collection: String) = MongoClient(uri).getDatabase(dbName).getCollection(collection)
}


object Mydatabase extends App{
  val collection = MongoConnector.getCollection("test")

  var doc: Document = Document("name" -> "MongoDB", "type" -> "database", "count" -> 1,
   "info" -> Document("x" -> 203, "y" -> 102))
  //////////
  // insert one doc in collectioin
  //////////
  /*val res= collection.insertOne(doc)
  res.subscribe(new Observer[Any] {
    override def onNext(result: Any): Unit = println("inserted")
    override def onError(e: Throwable): Unit = println("failed")
    override def onComplete(): Unit = println("completed")
  })*/
  //Thread.sleep(5000)
 // val res = collection.find(equal("name","MongoDB"))
  //val res=Await.result(collection.find(equal("name","MongoDB")).first().toFuture(),10 seconds )
  //println(res)
 // println(res)
 //val res = Await.result(collection.updateOne(equal("name", "MongoDB"), set("name", "Mongodb")).toFuture(),10 seconds)
 // println(res)

  val res=Await.result(collection.find(equal("name","Mongodb")).first().toFuture(),10 seconds )
  //println(res)
  //val res = Await.result(collection.deleteOne(equal("name","rajes")).toFuture(),10 seconds)
  println(res)
}
