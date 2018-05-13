package org.tuubes.core.network

import com.electronwill.niol.NiolOutput
import com.electronwill.niol.network.tcp.ClientAttach

/** A network packet that can be written */
trait Packet {
  /** The packet's id in its protocol */
  def id: Int

  /** Writes this packet to the given output */
  def write(out: NiolOutput): Unit
}
