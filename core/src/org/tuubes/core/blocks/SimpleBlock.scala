package org.tuubes.core.blocks

import org.tuubes.core.worlds.BlockLocation
import org.tuubes.core.engine.{ActorMessage, AttributeStorage, ExecutionGroup}

/**
 * A simple block that carries no information other than its type and location.
 *
 * @param typ      the block's type
 * @param location the block's location
 */
final case class SimpleBlock(typ: BlockType[_], location: BlockLocation) extends Block {
  override def !(msg: ActorMessage)(implicit g: ExecutionGroup) = {}
  override def safely(f: AttributeStorage => Unit)(implicit g: ExecutionGroup) = {}
}
