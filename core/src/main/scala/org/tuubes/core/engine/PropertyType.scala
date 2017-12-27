package org.tuubes.core.engine

/**
 * A type of [[Property]].
 *
 * @author TheElectronWill
 */
sealed trait PropertyType[A] extends Any {
	private[tuubes] def id: Int
	def create(value: A, newlyAdded: Boolean = false): Property[A]
}
final class SimplePropertyType[A](val id: Int) extends AnyVal with PropertyType[A] {
	override def create(value: A, newlyAdded: Boolean): Property[A] = {
		new SimpleProperty[A](this, value, newlyAdded)
	}
}
final class MemorizedPropertyType[A](val id: Int) extends AnyVal with PropertyType[A] {
	override def create(value: A, newlyAdded: Boolean): Property[A] = {
		new MemorizedProperty[A](this, value, newlyAdded)
	}
}