package server

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object MyServer extends App {

  implicit val system = ActorSystem("My-System")
  val service = system.actorOf(Props[SampleServiceActor], "Actor-Service")

  val connection = RabbitMQConnection.getConnection
  val channel = connection.createChannel
  channel.queueDeclare(Config.RABBITMQ_QUEUE, false, false, false, null)

  val sendingActor = system.actorOf(Props(new SendingActor(channel, Config.RABBITMQ_QUEUE)), "Actor-Sender")
  val listeningActor = system.actorOf(Props(new ListeningActor(channel, Config.RABBITMQ_QUEUE)), "Actor-Listener")

  listeningActor ! 1
  IO(Http) ! Http.Bind(service, "localhost", 8080)

}
