package org.tuubes.core.network

import com.electronwill.niol.network.tcp.ScalableSelector
import org.tuubes.core.TuubesServer.logger

/** Object for network bases, manages the TCP Selector */
object NetworkSystem {
  private val errorHandler = (e: Exception) => {
    logger.error("Error in the TCP ScalableSelector", e)
  }
  private val startHandler = () => {
    logger.info("TCP ScalableSelector started")
  }
  private val stopHandler = () => {
    logger.info("TCP ScalableSelector stopped")
  }

  /** The server's ScalableSelector that handles the TCP connections on several ports */
  val selector = new ScalableSelector(errorHandler, startHandler, stopHandler)

  /** Starts the network system */
  def start() = {
    logger.info("Starting the network system...")
    selector.start("Tuubes:Niol-TCP")
  }

  /** Stops the network system */
  def stop() = {
    logger.info("Stopping the network system...")
    selector.stop()
  }
}
