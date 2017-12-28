package org.tuubes.core.engine

import scala.collection.mutable

/**
 * Stores all the "properties" of a [[GameObject]].
 *
 * @author TheElectronWill
 */
final class PropertyStorage extends Iterable[Property[_]] {
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
			propertiesMap(prop.id) = new SimpleProperty[A](prop, initialValue)
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
			propertiesMap(prop.id) = new SimpleProperty[A](prop, value)
		} else {
			p.asInstanceOf[Property[A]].set(value)
		}
	}

	/**
	 * Adds a listener to a property.
	 *
	 * @param prop the PropertyType
	 * @param l    the listener
	 * @tparam A the property value's type
	 * @return Some(listeningKey) if the property exists, else None
	 */
	def listen[A](prop: PropertyType[A], l: SimpleValueListener[A]): Option[ListeningKey[A]] = {
		val p = propertiesMap.get(prop.id)
		p.map(_.asInstanceOf[Property[A]].addListener((oldV, newV) => l.onChange(newV)))
	}

	/**
	 * Adds a listener to a property, and ensure that the property is a [[MemorizedProperty]].
	 * <p>
	 * If you don't need to know the old value of the property, please use a
	 * [[SimpleValueListener]] with the other `listen` method.
	 *
	 * @param prop the PropertyType
	 * @param l    the listener
	 * @tparam A the property value's type
	 * @return Some(listeningKey) if the property exists, else None
	 */
	def listen[A](prop: PropertyType[A], l: ValueListener[A]): Option[ListeningKey[Property[A]]] = {
		val p = propertiesMap.get(prop.id)
		p match {
			case Some(sp: SimpleProperty[A]) =>
				// We need a MemorizedProperty but have a SimpleProperty: let's change
				val mp = new MemorizedProperty[A](sp)
				Some(mp.addListener(l))
			case Some(mp: MemorizedProperty[A]) =>
				// We have the MemorizedProperty
				Some(mp.addListener(l))
			case _ =>
				// The property doesn't exist
				None
		}
	}

	/**
	 * Removes a listener from a property.
	 *
	 * @param prop the PropertyType
	 * @param key  the listener's key
	 */
	def unlisten[A](prop: PropertyType[A], key: ListeningKey[Property[A]]): Unit = {
		val p = propertiesMap.get(prop.id)
		p.foreach(_.asInstanceOf[Property[A]].removeListener(key))
	}

	/**
	 * Creates an iterator over the properties.
	 *
	 * @return a new iterator that iterates over all the properties of this storage
	 */
	override def iterator: Iterator[Property[_]] = {
		propertiesMap.valuesIterator
	}
}