package org.tuubes.core.network

import com.electronwill.niol.Reader
import com.electronwill.niol.network.tcp.ClientAttach
import org.tuubes.core.TuubesServer
import org.tuubes.core.engine.BiObservable

import scala.collection.mutable

/** Trait for packet companion objects */
abstract class PacketObj[C <: ClientAttach, P <: Packet] extends BiObservable[P, C] with Reader[P] {
  /** The packet's id */
  val id: Int

  /** Handles the packet */
  def handle(packet: P, client: C): Unit = {
    TuubesServer.logger.debug(s"Received packet ${packet.getClass}")
    for (observer <- observers) {
      observer(packet, client)
    }
  }

  private[this] val observers = new mutable.ArrayBuffer[(P, C) => Unit]()

  override def subscribe(f: (P, C) => Unit): Unit = {
    observers += f
  }
}
