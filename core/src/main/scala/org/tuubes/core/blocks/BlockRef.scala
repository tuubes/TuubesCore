package org.tuubes.core.blocks

import org.tuubes.block.BlockType
import org.tuubes.core.engine.{ListenKey, ValueListener}

/**
 * @author TheElectronWill
 */
trait BlockRef {
	val x: Int
	val y: Int
	val z: Int
	def cachedType: BlockType // Returns the last type known by this BlockType
	def setType(t: BlockType): Unit // Sends SetBlock(...) to the world
	def listen(l: ValueListener[BlockType]): ListenKey[BlockType]
	def unlisten(key: ListenKey[BlockType])
}