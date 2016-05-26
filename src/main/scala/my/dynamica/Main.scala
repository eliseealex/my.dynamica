package my.dynamica

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import my.dynamica.http.{WatcherServer, WatcherSettings}
import my.dynamica.influx.InfluxDbManager

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

  private val influxDb = system.actorOf(Props[InfluxDbManager])
  private val watcherServer = system.actorOf(WatcherServer.props(influxDb))

  StdIn.readLine()

  system.terminate()
}
