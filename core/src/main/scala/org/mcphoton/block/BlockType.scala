package org.mcphoton.block

import org.mcphoton.{GameRegistry, Type}

/**
 * @author TheElectronWill
 */
class BlockType(n: String) extends Type[BlockType](n) {
	private[mcphoton] val (id, additionalData) = GameRegistry.registerBlock(uniqueName, this)
	private[mcphoton] def fullId: Int = id << 4 | additionalData.getOrElse(0)
}