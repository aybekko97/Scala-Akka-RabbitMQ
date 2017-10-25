package server

import akka.actor.{Actor, ActorContext}
import server.MyServer._
import spray.routing.HttpService


class SampleServiceActor extends Actor with SampleRoute {
  def actorRefFactory = context
  def receive = runRoute(route)
}

trait SampleRoute extends HttpService {
  import spray.httpx.SprayJsonSupport._
  import User._
  import spray.http.MediaTypes

  val route = {
    respondWithMediaType(MediaTypes.`application/json`) {
      get {
        path("users" / IntNumber) { id =>
          val user: Option[User] = User.get(id)
          if (user.isEmpty) complete(s""" {"details": "No user with id $id."} """)
          complete(user)
        }
      } ~ post {
        path("users") {
          entity(as[User]) { user =>
            sendingActor ! CreateUser(user.name, user.surname)
            var x = User(user.name, user.surname)
            x.save()
            complete(x)
          }
        }
      }
    }
  }
}