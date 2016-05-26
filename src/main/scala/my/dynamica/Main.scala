package my.dynamica

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging

import scala.io.StdIn

/**
  *
  * @author Aleksandr Eliseev
  */
object Main extends App with StrictLogging {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val settings = WatcherSettings(system)

  logger.info(s"I'm ready. World, hold on! Server will start at ${settings.interface}:${settings.port}")

  private val watcherServer: ActorRef = system.actorOf(WatcherServer.props(null))

  StdIn.readLine()

  system.terminate()
}
