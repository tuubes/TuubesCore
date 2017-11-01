package org.mcphoton.item

import org.mcphoton.{GameRegistry, Type}

/**
 * @author TheElectronWill
 */
class ItemType(n: String) extends Type[ItemType](n) {
	private[mcphoton] val (id, additionalData) = GameRegistry.registerItem(uniqueName, this)
	private[mcphoton] def fullId: Int = id << 4 | additionalData.getOrElse(0)
}