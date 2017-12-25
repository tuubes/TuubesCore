package org.tuubes.block

import org.tuubes.{GameRegistry, Type}

/**
 * @author TheElectronWill
 */
class BlockType(n: String) extends Type[BlockType](n) {
	private[tuubes] val (id, additionalData) = GameRegistry.registerBlock(uniqueName, this)
	private[tuubes] def fullId: Int = id << 4 | additionalData.getOrElse(0)
}