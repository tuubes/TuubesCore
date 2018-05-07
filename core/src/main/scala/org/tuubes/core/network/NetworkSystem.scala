package org.tuubes.core.network

import com.electronwill.niol.network.tcp.ScalableSelector
import org.tuubes.core.TuubesServer.logger

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
  val selector = new ScalableSelector(errorHandler, startHandler, stopHandler)
}