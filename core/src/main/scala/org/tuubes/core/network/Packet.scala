package org.tuubes.core.network

import com.electronwill.niol.NiolOutput

/** A network packet that can be written */
trait Packet {
  /** The packet's id in its protocol */
  final def id: Int = obj.id

  /** The packet object (usually a companion object of the packet class) */
  def obj: PacketObj[this.type]

  /** Writes this packet to the given output */
  def write(out: NiolOutput): Unit
}
