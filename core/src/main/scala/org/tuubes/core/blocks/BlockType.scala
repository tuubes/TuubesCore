package org.tuubes.core.blocks

import org.tuubes.core.{Type, TypeRegistry}

/**
 * A type of block.
 *
 * @author TheElectronWill
 */
abstract class BlockType(n: String) extends Type[BlockType](n, BlockType) {}

/**
 * Companion object and registry of block types.
 */
object BlockType extends TypeRegistry[BlockType] {}