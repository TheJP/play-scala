

import play.api.Application
import play.Logger
import play.api.GlobalSettings

object Global extends GlobalSettings {
  override def onStart(app: Application) = {
    Logger.info("Server starting...")
  }
}