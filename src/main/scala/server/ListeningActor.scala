package server

import com.rabbitmq.client.Channel
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.DefaultConsumer
import java.io.IOException

import akka.actor.Actor


class ListeningActor(channel: Channel, queue: String) extends Actor {
  def receive = {
    case _ => startReceiving
  }

  def startReceiving = {
    val consumer = new DefaultConsumer(channel) {
      @throws[IOException]
      def handleDelivery(consumerTag: String, envelope: Nothing, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
        val message = new String(body, "UTF-8")
        println(" [x] Received '" + message + "'")
      }
    }
    channel.basicConsume(Config.RABBITMQ_QUEUE, true, consumer)
  }
}