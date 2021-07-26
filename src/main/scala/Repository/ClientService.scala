package Repository

import akka.actor.ActorSystem

object ClientService {
  lazy val actorSystem = ActorSystem("RepositoryContext")
  lazy val scheduler = actorSystem.scheduler
  implicit lazy val executionContext = actorSystem.dispatcher
}
