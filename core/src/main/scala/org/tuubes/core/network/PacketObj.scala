package org.tuubes.core.network

import com.electronwill.niol.NiolInput

/** Trait for packet companion objects */
trait PacketObj[P <: Packet] {
  /** The packet's id */
  def id: Int

  /** Reads a packet of this type from the given input */
  def read(in: NiolInput): P
}
