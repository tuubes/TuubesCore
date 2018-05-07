package org.tuubes.core.network

trait PacketBuilder[P <: Packet, C] {
  def build()(implicit evidence P =:= C): P
}