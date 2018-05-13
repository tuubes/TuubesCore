package org.tuubes.core

import TuubesServer._
import org.tuubes.core.network.NetworkSystem

object ShutdownHandler extends Runnable {
  override def run(): Unit = {
    logger.info("Time to sleep!")
    logger.info("Unloading the plugins...")
    PluginLoader.unloadAll()

    logger.info("Stopping the server...")
    NetworkSystem.stop()

    logger.info(s"Tuubes $Version shuts down.")
  }
}
