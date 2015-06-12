package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def socket = WebSocket.acceptWithActor[String, String] { request => out =>
    ChatWebsocketObj.props(out)
  }

}
