package reactivedropbox

import reactivedropbox.core.AppInfo
import com.dropbox.core.{DbxAppInfo, DbxRequestConfig, DbxWebAuthNoRedirect}
import java.util.Locale

/**
 * Handles authentication
 */
object Authenticator {

  val appInfo = AppInfo(Global.config.getString("api.key"), Global.config.getString("api.secret"))
  val config = new DbxRequestConfig("Robobox/0.1", Locale.getDefault.toString)

  def requestAuthorizationUrl = {
    val webAuth = new DbxWebAuthNoRedirect(config, new DbxAppInfo(appInfo.key, appInfo.secret))
    webAuth.start()
  }

}
