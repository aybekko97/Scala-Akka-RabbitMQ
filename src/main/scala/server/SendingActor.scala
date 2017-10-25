package server

import akka.actor.Actor
import com.rabbitmq.client.Channel

case class CreateUser(name: String, surname: String)
case class DeleteUser(id: Int)

class SendingActor(channel: Channel, queue: String) extends Actor {
  def receive = {
    case CreateUser(name, surname) =>
      val msg = s"create-{$name,$surname}"
      channel.basicPublish("", queue, null, msg.getBytes())
    case DeleteUser(id) =>
      val msg = s"delete-{$id}"
      channel.basicPublish("", queue, null, msg.getBytes())
  }
}