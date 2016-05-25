package my.dynamica

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.Config

/**
  *
  * @author eliseev
  */
class ServerSettingsImpl(config: Config) extends Extension {
  val interface: String = config.getString("serverSettings.interface")
  val port: Int = config.getInt("serverSettings.port")
}


object ServerSettings extends ExtensionId[ServerSettingsImpl] with ExtensionIdProvider {
  override def lookup() = ServerSettings

  override def createExtension(system: ExtendedActorSystem): ServerSettingsImpl =
    new ServerSettingsImpl(system.settings.config)
}


