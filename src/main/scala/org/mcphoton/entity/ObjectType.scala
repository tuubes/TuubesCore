package org.mcphoton.entity

import org.mcphoton.{GameRegistry, Type}

/**
 * @author TheElectronWill
 */
class ObjectType(n: String) extends Type[ObjectType](n) {
	override private[mcphoton] val id = GameRegistry.registerEntityObject(uniqueName, this)
}