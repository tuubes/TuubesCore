package org.tuubes.core.blocks

import com.electronwill.util.BlockLocation
import org.tuubes.core.engine.{Actor, AttributeStorage, ExecutionGroup}

/**
 * Represents an existing block.
 */
trait Block extends Actor {
  /** @return the block's type */
  def typ: BlockType[_]

  /** @return the block's location */
  def location: BlockLocation

  /**
   * Manipulates the block's attributes in a thread-safe manner.
   * If the block has no attribute the function may never be called.
   *
   * @param f            the function to execute
   * @param currentGroup the current ExecutionGroup
   */
  def safely(f: AttributeStorage => Unit)(implicit currentGroup: ExecutionGroup): Unit
}
