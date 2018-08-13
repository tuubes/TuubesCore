package org.tuubes.core.blocks

import com.electronwill.utils.Vec3i

/**
 * An area that contains blocks.
 */
trait BlockArea extends Iterable[Block] {
  /**
   * Checks if the area contains some position.
   *
   * @param pos the position
   * @return true if the area contains the position, false otherwise
   */
  def contains(pos: Vec3i): Boolean

  /**
   * Gets the type of the block at the given position.
   *
   * @return the type of block the at the given index
   */
  def getType(pos: Vec3i): BlockType[_]

  /**
   * Puts a new block of the given type in the area.
   *
   * @param pos the block's position
   * @param t   the type to set
   */
  def setType(pos: Vec3i, t: BlockType[_]): Block

  /**
   * Gets the block at the given position.
   *
   * @return the block at the given index
   */
  def getRef(pos: Vec3i): Block

  /**
   * Captures the state of a block.
   *
   * @param pos the block's position
   * @return the captured, coherent state of the block
   */
  def captureState(pos: Vec3i): BlockState

  /**
   * Puts a new Block created from the given BlockState in the area.
   *
   * @param pos the block's position
   * @param s   the state to use
   */
  def applyState(pos: Vec3i, s: BlockState): Unit

  /**
   * Iterates on the BlockTypes. Avoids to create Block instances for simple blocks.
   */
  def types: Iterable[BlockType[_]]

  /**
   * Iterates on the positions. Avoids to create Block instances for simple blocks.
   */
  def positions: Iterable[Vec3i]
}
