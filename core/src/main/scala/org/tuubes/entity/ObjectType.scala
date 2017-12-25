package org.tuubes.entity

import org.tuubes.{GameRegistry, Type}

/**
 * @author TheElectronWill
 */
class ObjectType(n: String) extends Type[ObjectType](n) {
	override private[tuubes] val id = GameRegistry.registerEntityObject(uniqueName, this)
}