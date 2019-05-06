package com.electronwill.algebra

import scala.language.implicitConversions

/**
 * Value class that allows to left-multiply vectors by numerical values, like this:
 * `2.5*vector` in addition to `vector*2.5`.
 *
 * @param value the scalar value
 */
final class Scalar(val value: Double) extends AnyVal {
  def *(v: Vec3d): Vec3d = v * this.value
  def *(v: MutableVec3d): MutableVec3d = v * this.value

  def *(m: Matrix3d): Matrix3d = m * this.value
  def *(m: MutableMatrix3d): MutableMatrix3d = m * this.value
  def *(m: DiagonalMatrix): DiagonalMatrix = m * this.value
}
object Scalar {
  implicit def numberToScalar(nu: Number): Scalar = new Scalar(nu.doubleValue)
  implicit def numberToScalar(d: Double): Scalar = new Scalar(d)
  implicit def numberToScalar(f: Float): Scalar = new Scalar(f.toDouble)
  implicit def numberToScalar(l: Long): Scalar = new Scalar(l.toDouble)
  implicit def numberToScalar(i: Int): Scalar = new Scalar(i.toDouble)
}
