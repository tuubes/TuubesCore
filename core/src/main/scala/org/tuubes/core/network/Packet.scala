package org.tuubes.core.network

import com.electronwill.niol.NiolOutput
import com.electronwill.niol.network.tcp.ClientAttach

/** A network packet that can be written */
trait Packet[C <: ClientAttach] {
  /** The packet's id in its protocol */
  final def id: Int = obj.id

  /** The packet object (usually a companion object of the packet class) */
  def obj: PacketObj[this.type, C]

  /** Writes this packet to the given output */
  def write(out: NiolOutput): Unit
}
