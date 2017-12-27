package org.tuubes.core.engine

import scala.collection.mutable

/**
 * Stores all the "properties" of a [[GameObject]].
 *
 * @author TheElectronWill
 */
final class PropertyStorage {
	/**
	 * Map ID => Property
	 */
	private[this] val propertiesMap = new mutable.LongMap[Property[_]]

	/**
	 * Gets the value of a property.
	 *
	 * @param prop the PropertyType
	 * @tparam A the value's type
	 * @return Some(value) if this storage contains the property, None otherwise
	 */
	def apply[A](prop: PropertyType[A]): Option[A] = {
		propertiesMap.get(prop.id).map(_.get.asInstanceOf[A])
	}

	/**
	 * Updates an existing property.
	 *
	 * @param prop     the PropertyType
	 * @param newValue the new value to set
	 * @tparam A the value's type
	 * @return true if it has been updated, false if this storage doesn't contain the property
	 */
	def update[A](prop: PropertyType[A], newValue: A): Boolean = {
		val p = propertiesMap.get(prop.id)
		p.foreach(_.asInstanceOf[Property[A]].set(newValue))
		p.isDefined
	}

	/**
	 * Adds a property if it doesn't exist.
	 *
	 * @param prop         the PropertyType
	 * @param initialValue the initial value to set
	 * @tparam A the value's type
	 * @return true if it has been added, false if this storage already contains the property
	 */
	def +=[A](prop: PropertyType[A], initialValue: A): Boolean = {
		if (!propertiesMap.contains(prop.id)) {
			propertiesMap(prop.id) = prop.create(initialValue, true)
			true
		} else {
			false
		}
	}

	/**
	 * If the property exists, updates its value, else adds it to the storage.
	 *
	 * @param prop  the PropertyType
	 * @param value the value to set
	 * @tparam A the value's type
	 */
	def put[A](prop: PropertyType[A], value: A): Unit = {
		val p = propertiesMap.getOrNull(prop.id)
		if (p eq null) {
			propertiesMap(prop.id) = prop.create(value, true)
		} else {
			p.asInstanceOf[Property[A]].set(value)
		}
	}
}