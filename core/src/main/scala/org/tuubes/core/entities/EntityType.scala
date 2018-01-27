package org.tuubes.core.entities

import com.electronwill.collections.RecyclingIndex
import org.tuubes.core.Type

import scala.collection.mutable

/**
 * A type of entity.
 *
 * @author TheElectronWill
 */
abstract class EntityType(n: String, id: Int) extends Type[EntityType](n) {
	private[tuubes] val internalId = id
}
object EntityType {
	private val ids = new RecyclingIndex[EntityType]()
	private val names = new mutable.AnyRefMap[String, EntityType]()

	def apply(uniqueName: String): Option[EntityType] = {
		names.get(uniqueName)
	}

	private[tuubes] def apply(internalId: Int): Option[EntityType] = {
		ids(internalId)
	}

	private[tuubes] def register(t: EntityType): Unit = {
		ids(t.internalId) = t
		names(t.uniqueName) = t
	}
}