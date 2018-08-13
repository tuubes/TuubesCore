package org.tuubes.core.blocks

/**
 * A BlockState describes the state of a block. It doesn't track the current state of an existing
 * block but rather stores a consistent snapshot of its state.
 */
trait BlockState {
  /** @return the block's type */
  def typ: BlockType[_]
}
