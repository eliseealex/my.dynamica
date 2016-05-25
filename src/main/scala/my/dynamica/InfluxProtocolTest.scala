package my.dynamica

import com.paulgoldbaum.influxdbclient.{InfluxDB, Point}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  *
  * @author eliseev
  */
object InfluxProtocolTest extends App with StrictLogging {
  implicit val ec = ExecutionContext.global

  val influxdb = InfluxDB.connect("localhost", 8086)

  val database = influxdb.selectDatabase("dynamica-test")

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

  val point = Point("cpu")
    .addTag("host", "my.server")
    .addField("1m", 0.3)
    .addField("5m", 0.4)
    .addField("15m", 0.5)
  database.write(point)

  influxdb.close()
}
