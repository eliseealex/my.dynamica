package my.dynamica

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.Config

/**
  *
  * @author eliseev
  */
class WatcherSettingsImpl(config: Config) extends Extension {
  val interface: String = config.getString("serverSettings.interface")
  val port: Int = config.getInt("serverSettings.port")
}


object WatcherSettings extends ExtensionId[WatcherSettingsImpl] with ExtensionIdProvider {
  override def lookup() = WatcherSettings

  override def createExtension(system: ExtendedActorSystem): WatcherSettingsImpl =
    new WatcherSettingsImpl(system.settings.config)
}


