package reactivedropbox

import akka.actor.ActorSystem
import com.dropbox.core.DbxRequestConfig
import java.util.Locale
import com.typesafe.config.ConfigFactory

object Global {

  val config = ConfigFactory.load().getConfig("reactivedropbox")

  val system = ActorSystem("reactivedropbox")
  val requestConfig = new DbxRequestConfig(config.getString("clientid"), Locale.getDefault.toString)

}
