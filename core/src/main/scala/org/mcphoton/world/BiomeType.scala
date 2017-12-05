package org.mcphoton.world

import org.mcphoton.{GameRegistry, Type}

/**
 * @author TheElectronWill
 */
class BiomeType(n: String) extends Type[BiomeType](n) {
	override private[mcphoton] val id = GameRegistry.registerBiome(uniqueName, this)
}