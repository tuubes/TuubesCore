package org.mcphoton.entity

import org.mcphoton.{GameRegistry, Type}

/**
 * @author TheElectronWill
 */
class MobType(n: String) extends Type[MobType](n) {
	override private[mcphoton] val id = GameRegistry.registerEntityMob(uniqueName, this)
}