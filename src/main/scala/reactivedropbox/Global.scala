package reactivedropbox

import akka.actor.ActorSystem
import com.dropbox.core.DbxRequestConfig
import java.util.Locale

object Global {

  val system = ActorSystem("reactivedropbox")
  val requestConfig = new DbxRequestConfig("Robobox/0.1", Locale.getDefault.toString)

}
