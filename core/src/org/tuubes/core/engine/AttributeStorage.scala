package org.tuubes.core.engine

import scala.collection.mutable

/**
 * Stores all the "properties" of a [[GameObject]].
 *
 * @author TheElectronWill
 */
final class AttributeStorage extends Iterable[Attribute[_]] {

  /**
	 * Map ID => Property
	 */
  private[this] val idMap = new mutable.LongMap[Attribute[_]]

  /**
   * Gets the value of a property, or null.
   *
   * @param prop the PropertyType
   * @tparam A the value's type
   * @return The value if this storage contains the property, null otherwise
   */
  def getOrNull[A](prop: AttributeKey[A]): A = {
    val p = idMap.getOrNull(prop.id)
    if (p == null) null.asInstanceOf[A] else p.get.asInstanceOf[A]
  }

  /**
	 * Gets the value of a property.
	 *
	 * @param prop the PropertyType
	 * @tparam A the value's type
	 * @return Some(value) if this storage contains the property, None otherwise
	 */
  def apply[A](prop: AttributeKey[A]): Option[A] = {
    idMap.get(prop.id).map(_.get.asInstanceOf[A])
  }

  /**
	 * Updates an existing property.
	 *
	 * @param prop     the PropertyType
	 * @param newValue the new value to set
	 * @tparam A the value's type
	 * @return true if it has been updated, false if this storage doesn't contain the property
	 */
  def update[A](prop: AttributeKey[A], newValue: A): Boolean = {
    val p = idMap.get(prop.id)
    p.foreach(_.asInstanceOf[Attribute[A]].set(newValue))
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
  def +=[A](prop: AttributeKey[A], initialValue: A): Boolean = {
    if (!idMap.contains(prop.id)) {
      idMap(prop.id) = new SimpleAttribute[A](prop, initialValue)
      true
    } else {
      false
    }
  }

  /**
	 * Removes a property from the storage.
	 *
	 * @param prop the PropertyType
	 * @return true if it has been removed, false if this storage doesn't contain the property
	 */
  def -=(prop: AttributeKey[_]): Boolean = {
    idMap.remove(prop.id).isDefined
  }

  /**
	 * If the property exists, updates its value, else adds it to the storage.
	 *
	 * @param prop  the PropertyType
	 * @param value the value to set
	 * @tparam A the value's type
	 */
  def put[A](prop: AttributeKey[A], value: A): Unit = {
    val p = idMap.getOrNull(prop.id)
    if (p eq null) {
      idMap(prop.id) = new SimpleAttribute[A](prop, value)
    } else {
      p.asInstanceOf[Attribute[A]].set(value)
    }
  }

  /**
	 * Adds a listener to a property.
	 *
	 * @param prop the PropertyType
	 * @param l    the listener
	 * @tparam A the property value's type
	 * @return Some(listenKey) if the property exists, else None
	 */
  def listen[A](prop: AttributeKey[A],
                l: SimpleValueListener[A]): Option[ListenKey[Attribute[A]]] = {
    val p = idMap.get(prop.id)
    p.map(_.asInstanceOf[Attribute[A]].addListener((oldV, newV) => l.onChange(newV)))
  }

  /**
	 * Adds a listener to a property, and ensure that the property is a [[MemorizedAttribute]].
	 * <p>
	 * If you don't need to know the old value of the property, please use a
	 * [[SimpleValueListener]] with the other `listen` method.
	 *
	 * @param prop the PropertyType
	 * @param l    the listener
	 * @tparam A the property value's type
	 * @return Some(listenKey) if the property exists, else None
	 */
  def listen[A](prop: AttributeKey[A], l: ValueListener[A]): Option[ListenKey[Attribute[A]]] = {
    val p = idMap.get(prop.id)
    p match {
      case Some(sp: SimpleAttribute[A]) =>
        // We need a MemorizedProperty but have a SimpleProperty: let's change
        val mp = new MemorizedAttribute[A](sp)
        Some(mp.addListener(l))
      case Some(mp: MemorizedAttribute[A]) =>
        // We have the MemorizedProperty
        Some(mp.addListener(l))
      case _ =>
        // The property doesn't exist
        None
    }
  }

  /**
	 * Adds a listener to a property.
	 *
	 * @param prop the PropertyType
	 * @param l    the listener
	 * @tparam A the property value's type
	 * @return Some(listenRegistration) if the property exists, else None
	 */
  def rlisten[A](prop: AttributeKey[A],
                 l: SimpleValueListener[A]): Option[ListenRegistration[Attribute[A]]] = {
    listen(prop, l).map(
      new ListenRegistration(_, key => unlisten(prop, key))
    )
  }

  /**
	 * Adds a listener to a property, and ensure that the property is a [[MemorizedAttribute]].
	 * <p>
	 * If you don't need to know the old value of the property, please use a
	 * [[SimpleValueListener]] with the other `listen` method.
	 *
	 * @param prop the PropertyType
	 * @param l    the listener
	 * @tparam A the property value's type
	 * @return Some(listenRegistration) if the property exists, else None
	 */
  def rlisten[A](prop: AttributeKey[A],
                 l: ValueListener[A]): Option[ListenRegistration[Attribute[A]]] = {
    listen(prop, l).map(
      new ListenRegistration(_, key => unlisten(prop, key))
    )
  }

  /**
	 * Removes a listener from a property.
	 *
	 * @param prop the PropertyType
	 * @param key  the listener's key
	 */
  def unlisten[A](prop: AttributeKey[A], key: ListenKey[Attribute[A]]): Unit = {
    val p = idMap.get(prop.id)
    p.foreach(_.asInstanceOf[Attribute[A]].removeListener(key))
  }

  /**
	 * Creates an iterator over the properties.
	 *
	 * @return a new iterator that iterates over all the properties of this storage
	 */
  override def iterator: Iterator[Attribute[_]] = {
    idMap.valuesIterator
  }

  override def foreach[U](f: Attribute[_] => U): Unit = {
    idMap.foreachValue(f) // Efficient LongMap.foreachValue
  }
}
