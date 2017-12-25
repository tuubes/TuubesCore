package org.tuubes.item

import org.tuubes.{GameRegistry, Type}

/**
 * @author TheElectronWill
 */
class ItemType(n: String) extends Type[ItemType](n) {
	private[tuubes] val (id, additionalData) = GameRegistry.registerItem(uniqueName, this)
	private[tuubes] def fullId: Int = id << 4 | additionalData.getOrElse(0)

	def hasDataVariants: Boolean = additionalData.isDefined
}