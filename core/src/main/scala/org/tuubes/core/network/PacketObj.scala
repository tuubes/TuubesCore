package org.tuubes.core.network

trait PacketObj[P <: Packet] {
  def id: Int
  def read(in: NiolInput): P
}