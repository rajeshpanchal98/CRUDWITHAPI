name := "myfirstproject"

version := "0.1"

scalaVersion := "2.13.6"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.2.3"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.10",
  "com.typesafe.akka" %% "akka-http" % "10.2.2",
  "org.json4s" %% "json4s-jackson" % "3.7.0-M7",
  "org.json4s" %% "json4s-native" % "3.7.0-M7",
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
 // "org.mongodb.scala" %% "mongo-scala-driver" % "4.2.3",
  "org.slf4j" % "slf4j-simple" % "1.6.4"


)
val AkkaHttpVersion = "10.2.4"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
