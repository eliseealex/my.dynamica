package my.dynamica

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging

/**
  *
  * @author Aleksandr Eliseev
  */
object Main extends App with StrictLogging {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val settings = ServerSettings(system)

  logger.info(s"I'm ready. World, hold on! Server will start at ${settings.interface}:${settings.port}")

  system.terminate()
}
