package controllers

import akka.actor._
import actors.ChatServerActor

object ChatWebsocketObj {
  def props(out: ActorRef) = Props(new ChatWebsocket(out))
}

class ChatWebsocket(out: ActorRef) extends Actor {

  def receive = {
    case msg: String => {
      ChatServerActor.serverRef ! (out, msg)
    }
  }

  override def postStop = {
    ChatServerActor.serverRef ! (out, PoisonPill)
  }
}