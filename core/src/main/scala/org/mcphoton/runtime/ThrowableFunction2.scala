package org.mcphoton.runtime

import scala.language.implicitConversions

/**
 * A function T1,T2 => R that declares throwing a Throwable. The benefit over the standard
 * [[scala.Function2]] is that `ThrowableFunction2` can be used from java without try-catch.
 *
 * @author TheElectronWill
 */
trait ThrowableFunction2[-T1, -T2, +R] {
	/**
	 * @throws Throwable if an error occurs
	 */
	@throws[Throwable]
	def apply(v1: T1, v2: T2): R
}
object ThrowableFunction2 {
	implicit def fromFunction2[T1, T2, R](f: (T1, T2) => R): ThrowableFunction2[T1, T2, R] = f.apply _

	implicit def toFunction2[T1, T2, R](tf: ThrowableFunction2[T1, T2, R]): (T1, T2) => R = tf.apply
}