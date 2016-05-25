package my.dynamica

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging

/**
  *
  * @author eliseev
  */
object WatcherServer extends App with StrictLogging {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val route =
    logRequest("watcher") {
      path("watcher") {
        post {
          formFieldMap { params =>
            logger.debug(s"Obtained parameters: $params")

            saveEvent(params)
            complete("")
          }
        }
      }
    } ~
      path("watcher" / "status") {
        get {
          complete("Ok")
        }
      }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  def saveEvent: Map[String, String] => Unit = params => {
    logger.info(s"Saved $params")
  }

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  Console.readLine() // for the future transformations

  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ {
    system.terminate()
  }) // and shutdown when done

}

