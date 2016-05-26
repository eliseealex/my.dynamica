package my.dynamica.influx

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.Config

/**
  *
  * @author eliseev
  */
class InfluxDbSettings(config: Config) extends Extension {
  val host: String = config.getString("influxDb.host")
  val port: Int = config.getInt("influxDb.port")
  val username: String = config.getString("influxDb.username")
  val password: String = config.getString("influxDb.password")

  val database: String = config.getString("influxDb.database")
}


object InfluxDbSettings extends ExtensionId[InfluxDbSettings] with ExtensionIdProvider {
  override def lookup() = InfluxDbSettings

  override def createExtension(system: ExtendedActorSystem): InfluxDbSettings =
    new InfluxDbSettings(system.settings.config)
}
