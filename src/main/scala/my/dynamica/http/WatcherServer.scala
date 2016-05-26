package my.dynamica.http

import java.sql.Timestamp

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import my.dynamica.influx.InfluxDbManager.Write

/**
  *
  * @author eliseev
  */
object WatcherServer {
  def props(influxDb: ActorRef) = Props(new WatcherServer(influxDb))
}

class WatcherServer(influxDb: ActorRef) extends Actor with StrictLogging {


  implicit val ec = context.dispatcher
  implicit val system = context.system
  implicit val materializer = ActorMaterializer()

  private val settings = WatcherSettings(system)
  private val startTime: Timestamp = new Timestamp(context.system.startTime)

  override def receive: Receive = {
    case _ =>
  }

  val route =
    logRequestResult("watcher") {
      path("watcher") {
        post {
          formFieldMap { params =>
            logger.debug(s"Obtained parameters: $params")

            influxDb ! Write(params)
            complete("")
          }
        }
      } ~
        path("watcher" / "status") {
          get {
            complete(s"It works. Since $startTime")
          }
        }
    }

  val bindingFuture = Http().bindAndHandle(route, settings.interface, settings.port)

}

