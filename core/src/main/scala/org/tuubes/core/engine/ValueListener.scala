package org.tuubes.core.engine

/**
 * @author TheElectronWill
 */
trait ValueListener[A] {
	def onChange(oldValue: A, newValue: A): Unit
}
trait SimpleValueListener[A] {
	def onChange(newValue: A): Unit
}