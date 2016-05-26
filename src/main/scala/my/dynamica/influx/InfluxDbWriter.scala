package my.dynamica.influx

import akka.actor.{Actor, Props}
import com.paulgoldbaum.influxdbclient.{Database, Point}
import com.typesafe.scalalogging.StrictLogging
import my.dynamica.influx.InfluxDbWriter.{SendBatch, WritePoint}

import scala.collection.mutable
import scala.util.{Failure, Success}

/**
  *
  * @author eliseev
  */
object InfluxDbWriter {

  def props(influxDatabase: Database) = Props(new InfluxDbWriter(influxDatabase))

  case class WritePoint(point: Point)

  // Send batch request
  case object SendBatch
}

class InfluxDbWriter(influxDatabase: Database) extends Actor with StrictLogging {
  val batch = new mutable.Queue[Point]()

  implicit val ec = context.dispatcher

  override def receive: Receive = {
    case WritePoint(point) =>
      logger.debug(s"Scheduling point write [$point]")
      batch.enqueue(point)

    case SendBatch =>
      logger.trace("Draining data for batch request")

      val pointToSend: mutable.Seq[Point] = batch.dequeueAll((_) => true)

      if (pointToSend.nonEmpty) {

        influxDatabase.bulkWrite(pointToSend).onComplete({
          case Success(true) =>
            logger.debug("Batch write succeed")

          case Success(false) =>
            logger.warn("Batch write failed. Rescheduling points")
            batch ++= pointToSend

          case Failure(e) =>
            logger.warn("Batch write failed. Rescheduling points", e)
            batch ++= pointToSend
        })

      }
  }
}
