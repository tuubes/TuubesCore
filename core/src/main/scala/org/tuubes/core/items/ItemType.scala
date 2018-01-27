package org.tuubes.core.items

import com.electronwill.collections.RecyclingIndex
import org.tuubes.core.Type

import scala.collection.mutable

/**
 * A type of item.
 *
 * @author TheElectronWill
 */
abstract class ItemType(n: String, id: Int) extends Type[ItemType](n) {
	private[tuubes] val internalId = id
}
object ItemType {
	private val ids = new RecyclingIndex[ItemType]()
	private val names = new mutable.AnyRefMap[String, ItemType]()

	def apply(uniqueName: String): Option[ItemType] = {
		names.get(uniqueName)
	}

	private[tuubes] def apply(internalId: Int): Option[ItemType] = {
		ids(internalId)
	}

	private[tuubes] def register(t: ItemType): Unit = {
		ids(t.internalId) = t
		names(t.uniqueName) = t
	}
}