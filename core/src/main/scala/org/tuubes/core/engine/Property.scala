package org.tuubes.core.engine

/**
 * A [[GameObject]] property.
 *
 * @author TheElectronWill
 */
sealed abstract class Property[A](private[tuubes] val `type`: PropertyType[A],
								  protected[this] var value: A,
								  newlyAdded: Boolean = false) {
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
	 * Resets the modification cycle: just after this method, hasChanged will return false.
	 */
	def resetCycle(): Unit
}
final class SimpleProperty[A](t: SimplePropertyType[A], v: A, newlyAdded: Boolean)
	extends Property[A](t, v, newlyAdded) {
	override def set(newValue: A): Unit = {
		value = newValue
		changed = true
	}
	override def resetCycle(): Unit = {
		changed = false
	}
}
/**
 * A property for which we know both the current and previous value.
 *
 * @param t the type
 * @param v the initial value
 * @tparam A the value's type
 */
final class MemorizedProperty[A](t: MemorizedPropertyType[A], v: A, newlyAdded: Boolean)
	extends Property[A](t, v, newlyAdded) {
	private[this] var old: A = value

	override def set(newValue: A): Unit = {
		value = newValue
		changed = (newValue != old)
	}
	override def resetCycle(): Unit = {
		changed = false
		old = value
	}
	/**
	 * @return the old value
	 */
	def getOld: A = old
}