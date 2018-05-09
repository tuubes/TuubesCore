package org.tuubes.core.network

import com.electronwill.niol.NiolInput

/** Trait for packet companion objects */
trait PacketObj[P <: Packet] {
  def id: Int
  def read(in: NiolInput): P
}
