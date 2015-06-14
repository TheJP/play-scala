package actors

import akka.actor._
import play.api.libs.json._
import play.api.libs.json.Json.toJson
import scala.collection.mutable.HashMap
import scala.collection.immutable.Map
import play.libs.Akka

trait Msg {
  def json : JsValue
}

case object Login { val msgType = 11 }
case class Login(username: String) extends Msg {
  def json = {
    toJson(Map(
      "type" -> toJson(Login.msgType),
      "username" -> toJson(username)
    ))
  }
}

case object Logout { val msgType = 12 }
case class Logout()
case class LogoutResponse(username: String) extends Msg {
  def json = {
    toJson(Map(
      "type" -> toJson(Logout.msgType),
      "username" -> toJson(username)
    ))
  }
}

case object SendMessage { val msgType = 31 }
case class SendMessage(msg: String)
case class SendMessageResponse(msg: String, username: String) extends Msg {
  def json = {
    toJson(Map(
      "type" -> toJson(SendMessage.msgType),
      "msg" -> toJson(msg),
      "username" -> toJson(username)
    ))
  }
}

case object MouseMove { val msgType = 71 }
case class MouseMove(username: String, x: Double, y: Double) extends Msg {
  def json = {
    toJson(Map(
      "type" -> toJson(MouseMove.msgType),
      "username" -> toJson(username),
      "x" -> toJson(x),
      "y" -> toJson(y)
    ))
  }
}

object ChatServerActor {
  val serverRef: ActorRef = Akka.system.actorOf(Props[ChatServerActor])
}

class ChatServerActor extends Actor {
  var users = new HashMap[ActorRef, String]

  def receive = {
    case (out: ActorRef, msg: String) => {
      val request = Json.parse(msg)
      handle(out, toRequest(request))
    }
    case (out: ActorRef, PoisonPill) => {
      handle(out, Some(Logout()))
    }
  }

  def toRequest(request: JsValue) = {
    val msgType = (request \ "type").asOpt[Int]
    msgType match {
      case None => { None }
      case Some(Login.msgType) => {
        val username = (request \ "username").asOpt[String]
        if(username == None) None else Some(Login(username.get))
      }
      case Some(SendMessage.msgType) => {
        val msg = (request \ "msg").asOpt[String]
        msg.map { msg => SendMessage(msg) } //alternative syntax to the above if/else expression
      }
      case Some(MouseMove.msgType) => {
        val username = (request \ "username").asOpt[String]
        val x = (request \ "x").asOpt[Double]
        val y = (request \ "y").asOpt[Double]
        if(username == None || x == None || y == None) None else Some(MouseMove(username.get, x.get, y.get))
      }
      case _ => { None } //Ignore any other case
    }
  }

  /**
   * Success Response
   */
  def toResponse(response: Msg, notify: Boolean = true): String = {
    Json.stringify(
      response.json.as[JsObject] +
      ("notify" -> toJson(notify)) +
      ("success" -> toJson(true))
    )
  }

  /**
   * Error Response
   */
  def toError(response: Msg): String = ???

  def sendAll(json: String) = users.foreach(pair => pair._1 ! json)

  def handle(out: ActorRef, request: Option[Any]) : Unit = request match {
    case Some(Login(username)) => {
      users += (out -> username)
      val notification = toResponse(Login(username))
      sendAll(notification)
    }
    case Some(Logout()) => {
      val username = users.get(out)
      username.foreach { name => {
        users.remove(out)
        sendAll(toResponse(LogoutResponse(name), true))
      }}
    }
    case Some(SendMessage(msg)) => {
      val username = users.get(out)
      if(username != None){
        if(msg.trim().isEmpty()){
          out ! toResponse(SendMessageResponse("STOP SPAM", "system"), true)
        } else {
          val notification = toResponse(SendMessageResponse(msg, username.get))
          sendAll(notification)
        }
      }
    }
    case Some(r: MouseMove) => {
      sendAll(toResponse(r))
    }
    case _ => {} //Ignore any other case
  }
}