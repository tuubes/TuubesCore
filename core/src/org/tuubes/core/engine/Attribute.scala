package org.tuubes.core.engine

import com.electronwill.collections.RecyclingIndex

/**
 * A [[GameObject]] attribute. It contains data shared with all the object's behaviors.
 *
 * @author TheElectronWill
 */
sealed abstract class Attribute[A](private[engine] val typ: AttributeKey[A],
                                   protected[this] var value: A,
                                   newlyAdded: Boolean,
                                   private[tuubes] val listeners: RecyclingIndex[ValueListener[A]]) {
  protected[this] var changed = newlyAdded

  /**
	 * @return the current value
	 */
  final def get: A = value

  /**
	 * Updates the attribute.
	 *
	 * @param newValue the new value to set
	 */
  def set(newValue: A): Unit

  /**
	 * @return true if the attribute
	 */
  final def hasChanged: Boolean = changed

  /**
	 * Ends the modification cycle: notifies the listeners and resets the "hasChanged" state.
	 */
  def endCycle(): Unit

  def addListener(listener: ValueListener[A]): ListenKey[Attribute[A]] = {
    new ListenKey(listeners += listener)
  }

  def removeListener(key: ListenKey[Attribute[A]]): Unit = {
    listeners.remove(key.id)
  }
}
final class SimpleAttribute[A](t: AttributeKey[A],
                               v: A,
                               newlyAdded: Boolean = true,
                               listeners: RecyclingIndex[ValueListener[A]] =
                                new RecyclingIndex[ValueListener[A]])
  extends Attribute[A](t, v, newlyAdded, listeners) {
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
 * A attribute for which we know both the current and previous value.
 *
 * @param t the type
 * @param v the initial value
 * @tparam A the value's type
 */
final class MemorizedAttribute[A](t: AttributeKey[A],
                                  v: A,
                                  newlyAdded: Boolean = true,
                                  listeners: RecyclingIndex[ValueListener[A]] =
                                   new RecyclingIndex[ValueListener[A]])
  extends Attribute[A](t, v, newlyAdded, listeners) {

  private[this] var old: A = value

  def this(sp: SimpleAttribute[A]) = {
    this(sp.typ, sp.get, false, sp.listeners)
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
