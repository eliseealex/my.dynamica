package my.dynamica.influx

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import com.paulgoldbaum.influxdbclient.{InfluxDB, Point}
import com.typesafe.scalalogging.StrictLogging
import my.dynamica.influx.InfluxDbManager.{Shutdown, Write}
import my.dynamica.influx.InfluxDbWriter.{SendBatch, WritePoint}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

/**
  *
  * @author eliseev
  */
object InfluxDbManager {
  case class Write(tags: Map[String, String])

  case object Shutdown
}

class InfluxDbManager extends Actor with StrictLogging {
  implicit val ec = ExecutionContext.global

  val influxDbSettings = InfluxDbSettings(context.system)

  override def receive: Receive = {
    case Write(tags) =>
      val point = Point("analytic")
        .addField("count", 1)

      val taggedPoint = tags.foldLeft(point)((point, entry) => point.addTag(entry._1, entry._2))

      influxDbWriter ! WritePoint(taggedPoint)

    case Shutdown =>
      database.close()
      influxDb.close()
  }

  val influxDb = InfluxDB.connect(influxDbSettings.host,
    influxDbSettings.port,
    influxDbSettings.username,
    influxDbSettings.password)

  val database = influxDb.selectDatabase("dynamica-test")
  database.exists() andThen {
    case Failure(e) => s"Failed $e"
    case Success(true) =>
    logger debug s"Database exists."
    case Success(false) =>
    logger info s"Database not existing, creating new database"

    database.create() andThen {
        case Failure(e) => logger warn s"Failed $e"
        case Success(q) => logger info s"Database created $q"
      }
  }

  val influxDbWriter = context.actorOf(InfluxDbWriter.props(database))

  val duration = Duration.create(300, TimeUnit.MILLISECONDS)

  context.system.scheduler.schedule(duration, duration, influxDbWriter, SendBatch)

}
