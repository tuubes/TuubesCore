package org.tuubes.core.network

import com.electronwill.niol.NiolInput
import com.electronwill.niol.network.tcp.ClientAttach

/** Trait for packet companion objects */
trait PacketObj[C <: ClientAttach, P <: Packet[C]] {
  /** The packet's id */
  def id: Int

  /** Reads a packet of this type from the given input */
  def read(in: NiolInput): P

  /** Handles the packet - TODO: handler list, Observable, etc. */
  def handle(packet: P, client: C): Unit = ()
}
