package org.tuubes.core.tasks

import scala.language.implicitConversions

/**
 * A function T => R that declares throwing a Throwable. The benefit over the standard
 * [[scala.Function1]] is that `ThrowableFunction1` can be used from java without try-catch and
 * is a type different from [[scala.Function1]], so we can have two methods.
 *
 * @author TheElectronWill
 */
trait ThrowableFunction1[-T, +R] {

  /**
	 * @throws Throwable if an error occurs
	 */
  @throws[Throwable]
  def apply(v1: T): R
}
object ThrowableFunction1 {
  implicit def fromFunction1[T, R](f: T => R): ThrowableFunction1[T, R] = f.apply(_)

  implicit def toFunction1[T, R](tf: ThrowableFunction1[T, R]): (T) => R = tf.apply
}
