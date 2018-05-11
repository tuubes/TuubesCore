package org.tuubes.core.network

import com.electronwill.niol.NiolOutput

/** A network packet that can be written */
trait Packet {
  final def id: Int = obj.id
  def obj: PacketObj[this.type]
  def write(out: NiolOutput): Unit
}
