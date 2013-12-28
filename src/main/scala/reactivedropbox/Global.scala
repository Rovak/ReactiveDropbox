package reactivedropbox

import akka.actor.{TypedProps, TypedActor, ActorSystem}
import com.dropbox.core.DbxRequestConfig
import java.util.Locale
import com.typesafe.config.ConfigFactory
import reactivedropbox.actors.{DefaultClient}

object Global {

  val config = ConfigFactory.load().getConfig("reactivedropbox")
  val system = ActorSystem("reactivedropbox")
  val requestConfig = new DbxRequestConfig(config.getString("clientid"), Locale.getDefault.toString)

  def client(code: String): Client = TypedActor(system).typedActorOf(TypedProps(classOf[Client], new DefaultClient(requestConfig, code)))

}
