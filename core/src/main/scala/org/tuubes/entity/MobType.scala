package org.tuubes.entity

import org.tuubes.{GameRegistry, Type}

/**
 * @author TheElectronWill
 */
class MobType(n: String) extends Type[MobType](n) {
	override private[tuubes] val id = GameRegistry.registerEntityMob(uniqueName, this)
}