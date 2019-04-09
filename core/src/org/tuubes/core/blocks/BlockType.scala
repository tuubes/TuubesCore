package org.tuubes.core.blocks

import com.electronwill.util.BlockLocation
import org.tuubes.core.{Type, TypeRegistry}

/**
 * A type of block.
 *
 * @author TheElectronWill
 */
abstract class BlockType[S <: BlockState](n: String) extends Type[BlockType[_]](n, BlockType) {
  /**
   * Creates a new [[Block]] instance for the block that is being created at the given
   * location. Returns None if no instance of [[Block]] is needed, which happens when the block
   * carries no information other than its type and doesn't update.
   *
   * @param loc the block's location
   * @return the block instance, or None if no Block instance is needed
   */
  protected[core] def newBlock(loc: BlockLocation): Option[Block] = None

  /**
   * Creates a new [[Block]] instance for the block that is being created at the given
   * location, and applies the given [[BlockState]]. Returns None if no instance of [[Block]] is
   * needed, which happens when the block carries no information other than its type and doesn't
   * update.
   *
   * @param loc the block's location
   * @return the block instance, or None if no Block instance is needed
   */
  protected[core] def newBlock(loc: BlockLocation, state: S): Option[Block] = None

  /**
   * Creates a [[BlockState]] with some default values for this BlockType.
   *
   * @return a new BlockState with default values
   */
  def newState(): S
}

/**
 * Companion object and registry of block types.
 */
object BlockType extends TypeRegistry[BlockType[_]] {}
