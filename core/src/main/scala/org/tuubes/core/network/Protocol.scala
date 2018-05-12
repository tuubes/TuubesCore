package org.tuubes.core.network

import com.electronwill.utils.IntBijection
import com.electronwill.niol.NiolInput
import com.electronwill.niol.network.tcp.ClientAttach
import org.tuubes.core.blocks.BlockType
import org.tuubes.core.engine.GameObject
import org.tuubes.core.entities.EntityType
import org.tuubes.core.items.ItemType

/**
 * A network protocol used by the server to communicate with one client.
 *
 * @author TheElectronWill
 */
trait Protocol[C <: ClientAttach] {
  def spawn(obj: GameObject): Unit
  def despawn(obj: GameObject): Unit
  //TODO more methods

  /**
	 * Registers a packet to this protocol
	 */
  def registerPacket[A <: Packet[C]](packet: PacketObj[C, A]): Unit

  /**
	 * Reads a packet from the given NiolInput.
	 */
  def readPacket(in: NiolInput): Packet[C]

  /**
	 * A bijection of block ids.
	 * [internal -> external] (direct) and [external -> internal] (inverse).
	 */
  def blockFunction: IntBijection[BlockType]

  /**
	 * A bijection of item ids.
	 * [internal -> external] (direct) and [external -> internal] (inverse).
	 */
  def itemFunction: IntBijection[ItemType]

  /**
	 * A bijection of entity ids.
	 * [internal -> external] (direct) and [external -> internal] (inverse).
	 */
  def entityFunction: IntBijection[EntityType]
}
