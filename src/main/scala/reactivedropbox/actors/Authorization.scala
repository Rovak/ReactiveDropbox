package reactivedropbox.actors

import akka.actor.Actor
import scala.concurrent.duration._
import com.dropbox.core.{DbxAppInfo, DbxWebAuthNoRedirect}
import reactivedropbox.Global
import reactivedropbox.core.AppInfo

/**
 *
 */
class Authorization extends Actor {

  /**
   * Request a new authorization URL and add the request to its pending cleanup cycle
   * @param appInfo Application Info
   * @return
   */
  def requestAuthorizationUrl(appInfo: AppInfo) = {
    val webAuth = new DbxWebAuthNoRedirect(Global.requestConfig, new DbxAppInfo(appInfo.key, appInfo.secret))
    webAuth.start()
  }

  /**
   * Start scheduling garbage collection of old requests
   */
  override def preStart() = {
    import scala.concurrent.ExecutionContext.Implicits.global
    context.system.scheduler.schedule(1 minute, 1 minute) {
      self ! CleanupPendingAuthorizations()
    }
  }

  def receive = {
    case RequestAuthorizationUrl(appinfo) =>
      sender ! requestAuthorizationUrl(appinfo)

  }
}


case class RequestAuthorizationUrl(appInfo: AppInfo)
case class CleanupPendingAuthorizations()
