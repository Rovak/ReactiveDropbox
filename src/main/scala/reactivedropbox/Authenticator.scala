package reactivedropbox

import com.dropbox.core._
import java.util.Locale
import scala.concurrent.Future
import reactivedropbox.core.AppInfo

/**
 * Handles authentication
 */
object Authenticator {

  case class AuthRequestWithRedirect(url: String, auth: DbxWebAuth)

  class SessionStore extends DbxSessionStore {
    var value = ""
    def get = value
    def clear = value = ""
    def set(newValue: String) = value = newValue
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  val appInfo = AppInfo(Global.config.getString("api.key"), Global.config.getString("api.secret"))
  val config = new DbxRequestConfig("Robobox/0.1", Locale.getDefault.toString)

  def webAuth = new DbxWebAuthNoRedirect(config, new DbxAppInfo(appInfo.key, appInfo.secret))

  def requestAuthorizationUrl = Future {
    val webAuth = new DbxWebAuthNoRedirect(config, new DbxAppInfo(appInfo.key, appInfo.secret))
    webAuth.start()
  }

  def requestAuthUrlWithRedirect(redirectUri: String) = Future {
    val webAuth = new DbxWebAuth(config, new DbxAppInfo(appInfo.key, appInfo.secret), redirectUri, new SessionStore)
    AuthRequestWithRedirect(webAuth.start(), webAuth)
  }

  def handleAuthorizationCode(code: String) = Future {

  }

}
