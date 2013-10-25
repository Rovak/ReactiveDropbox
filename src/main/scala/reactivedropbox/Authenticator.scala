package reactivedropbox

import reactivedropbox.core.AppInfo
import com.dropbox.core.{DbxAppInfo, DbxRequestConfig, DbxWebAuthNoRedirect}
import java.util.Locale

/**
 * Handles authentication
 */
object Authenticator {

  val appInfo = AppInfo("", "")
  val config = new DbxRequestConfig("ReactiveDropbox/0.1", Locale.getDefault.toString)

  def requestAuthorizationUrl = {
    val webAuth = new DbxWebAuthNoRedirect(config, new DbxAppInfo(appInfo.key, appInfo.secret))
    webAuth.start()
  }

}
