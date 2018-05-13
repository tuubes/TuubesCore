package org.tuubes.core.network

import com.electronwill.niol.NiolInput
import com.electronwill.niol.network.tcp.ClientAttach
import org.tuubes.core.TuubesServer
import org.tuubes.core.engine.BiObservable

import scala.collection.mutable

/** Trait for packet companion objects */
abstract class PacketObj[C <: ClientAttach, P <: Packet] extends BiObservable[P, C] {
  /** The packet's id */
  def id: Int

  /** Reads a packet of this type from the given input */
  def read(in: NiolInput): P

  /** Handles the packet */
  def handle(packet: P, client: C): Unit = {
    TuubesServer.logger.debug(s"Received packet ${packet.getClass}")
    for (observer <- observers) {
      observer(packet, client)
    }
  }

  private val observers = new mutable.ArrayBuffer[(P, C) => Unit]()

  override def subscribe(f: (P, C) => Unit): Unit = {
    observers += f
  }
}
