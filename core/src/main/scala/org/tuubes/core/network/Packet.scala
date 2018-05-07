package org.tuubes.core.network

import com.electronwill.niol.NiolOutput

trait Packet {
  def id: Int
  def write(out: NiolOutput): Unit
}