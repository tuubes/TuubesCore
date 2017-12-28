package org.tuubes.core.engine

/**
 * A [[Registration]] of a [[ListeningKey]]. Allows to unregister the listener easily.
 *
 * @author TheElectronWill
 */
final class ListeningRegistration[A](private[this] var key: ListeningKey[A],
									 private[this] val fCancel: ListeningKey[A] => ())
	extends Registration[A] {
	override def cancel(): Unit = fCancel(key)
	override def isValid: Boolean = (key ne null)
}