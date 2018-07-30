package org.tuubes.core

import better.files.File
import org.fusesource.jansi.AnsiConsole
import org.slf4j.LoggerFactory
import org.slf4j.impl.{LogLevel, PhotonLogger}
import org.tuubes.core.network.NetworkSystem
import org.tuubes.core.plugins.ScalaPluginLoader

/**
 * @author TheElectronWill
 */
object TuubesServer {
  private[core] val logger = LoggerFactory.getLogger("TuubesCore")

  final val Version: String = "0.6-alpha-snapshot"

  final val DirMain = File(System.getProperty("user.dir"))
  final val DirConfig: File = DirMain / "config"
  final val DirPlugins: File = DirMain / "plugins"
  final val DirWorlds: File = DirMain / "worlds"
  final val DirLogs: File = DirMain / "logs"

  final val PluginLoader = new ScalaPluginLoader

  def main(args: Array[String]): Unit = {
    Seq(DirConfig, DirPlugins, DirWorlds, DirLogs).foreach(_.createDirectories())
    logger.info("Tuubes core loading...")
    logger.warn("WARNING: This is an unreleased version of TuubesCore, potentially unstable")
    PhotonLogger.setLevel(LogLevel.DEBUG)

    AnsiConsole.systemInstall()
    // TODO read configuration(s?)

    logger.info("Let's load the plugins!")
    PluginLoader.load(DirPlugins.list)

    // TODO load the world(s)
    logger.info("Connecting to the world...")
    NetworkSystem.start()

    Runtime.getRuntime.addShutdownHook(new Thread(ShutdownHandler, "shutdown"))
    logger.info(s"Tuubes $Version is ready!")
  }
}
