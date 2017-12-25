package org.tuubes.runtime

import scala.language.implicitConversions

/**
 * A function T => R that declares throwing a Throwable. The benefit over the standard
 * [[scala.Function0]] is that `ThrowableFunction0` can be used from java without try-catch.
 *
 * @author TheElectronWill
 */
trait ThrowableFunction0[+R] {
	/**
	 * @throws Throwable if an error occurs
	 */
	@throws[Throwable]
	def apply(): R
}
object ThrowableFunction0 {
	implicit def fromFunction0[R](f: () => R): ThrowableFunction0[R] = () => f.apply()

	implicit def toFunction0[R](tf: ThrowableFunction0[R]): () => R = tf.apply _
}