package org.tuubes.core.network

import com.electronwill.niol.Writeable

/** A network packet that can be written */
trait Packet extends Writeable {
  /** The packet's id in its protocol */
  def id: Int
}
