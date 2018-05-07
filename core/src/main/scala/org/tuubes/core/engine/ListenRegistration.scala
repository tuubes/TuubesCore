package org.tuubes.core.engine

/**
 * A [[Registration]] of a [[ListenKey]]. Allows to unregister the listener easily.
 *
 * @author TheElectronWill
 */
final class ListenRegistration[A](private[this] val key: ListenKey[A],
								  private[this] var fCancel: ListenKey[A] => Unit)
	extends Registration[A] {
	override def cancel(): Unit = {
		fCancel(key)
		fCancel = null
	}
	override def isValid: Boolean = (fCancel != null)
}