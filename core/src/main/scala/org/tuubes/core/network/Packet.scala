package org.tuubes.core.network

import com.electronwill.niol.NiolOutput

/** A network packet that can be written */
trait Packet {
  def id: Int
  def write(out: NiolOutput): Unit
}
