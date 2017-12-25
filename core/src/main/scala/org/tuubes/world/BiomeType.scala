package org.tuubes.world

import org.tuubes.{GameRegistry, Type}

/**
 * @author TheElectronWill
 */
class BiomeType(n: String) extends Type[BiomeType](n) {
	override private[tuubes] val id = GameRegistry.registerBiome(uniqueName, this)
}