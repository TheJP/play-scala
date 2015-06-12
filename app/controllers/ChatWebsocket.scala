package controllers

import akka.actor._

object ChatWebsocketObj {
  def props(out: ActorRef) = Props(new ChatWebsocket(out))  
}

class ChatWebsocket(out: ActorRef) extends Actor {
  var counter : Int = 0
  def receive = {
    case msg: String => {
      out ! (counter + " I received your message: " + msg)
      counter += 1
    }
  }
}