package org.tuubes.core.engine

import com.electronwill.collections.RecyclingIndex

/**
 * A [[GameObject]] property.
 *
 * @author TheElectronWill
 */
sealed abstract class Property[A](private[tuubes] val `type`: PropertyType[A],
                                  protected[this] var value: A,
                                  newlyAdded: Boolean,
                                  private[tuubes] val listeners: RecyclingIndex[ValueListener[A]]) {
  protected[this] var changed = newlyAdded

  /**
	 * @return the current value
	 */
  final def get: A = value

  /**
	 * Updates the property.
	 *
	 * @param newValue the new value to set
	 */
  def set(newValue: A): Unit

  /**
	 * @return true if the property
	 */
  final def hasChanged: Boolean = changed

  /**
	 * Ends the modification cycle: notifies the listeners and resets the "hasChanged" state.
	 */
  def endCycle(): Unit

  def addListener(listener: ValueListener[A]): ListenKey[Property[A]] = {
    new ListenKey(listeners += listener)
  }

  def removeListener(key: ListenKey[Property[A]]): Unit = {
    listeners.remove(key.id)
  }
}
final class SimpleProperty[A](t: PropertyType[A],
                              v: A,
                              newlyAdded: Boolean = true,
                              listeners: RecyclingIndex[ValueListener[A]] =
                                new RecyclingIndex[ValueListener[A]])
    extends Property[A](t, v, newlyAdded, listeners) {
  override def set(newValue: A): Unit = {
    value = newValue
    changed = true
  }
  override def endCycle(): Unit = {
    if (changed) {
      val newValue = value
      for (listener <- listeners.valuesIterator) {
        listener.onChange(newValue, newValue)
      }
      changed = false
    }
  }
}

/**
 * A property for which we know both the current and previous value.
 *
 * @param t the type
 * @param v the initial value
 * @tparam A the value's type
 */
final class MemorizedProperty[A](t: PropertyType[A],
                                 v: A,
                                 newlyAdded: Boolean = true,
                                 listeners: RecyclingIndex[ValueListener[A]] =
                                   new RecyclingIndex[ValueListener[A]])
    extends Property[A](t, v, newlyAdded, listeners) {

  private[this] var old: A = value

  def this(sp: SimpleProperty[A]) = {
    this(sp.`type`, sp.get, false, sp.listeners)
  }

  override def set(newValue: A): Unit = {
    value = newValue
    changed = (newValue != old)
  }
  override def endCycle(): Unit = {
    if (changed) {
      val oldValue = old
      val newValue = value
      for (listener <- listeners.valuesIterator) {
        listener.onChange(oldValue, newValue)
      }
      changed = false
      old = newValue
    }
  }

  /**
	 * @return the old value
	 */
  def getOld: A = old
}
