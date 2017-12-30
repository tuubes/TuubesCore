package org.tuubes.core.engine

/**
 * A [[Registration]] of a [[ListenKey]]. Allows to unregister the listener easily.
 *
 * @author TheElectronWill
 */
final class ListenRegistration[A](private[this] var key: ListenKey[A],
								  private[this] val fCancel: ListenKey[A] => Unit)
	extends Registration[A] {
	override def cancel(): Unit = fCancel(key)
	override def isValid: Boolean = (key ne null)
}