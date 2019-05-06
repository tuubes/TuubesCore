package com.electronwill.algebra

import Scalar._ // to allow left scalar multiplication

/**
 * A 3-dimensional floating-point vector. This trait intentionally provides no mutation methods.
 */
trait Vec3d extends java.lang.Cloneable {
  /** @return the X coordinate */
  def x: Double

  /** @return the Y coordinate */
  def y: Double

  /** @return the Z coordinate */
  def z: Double

  /** @return the X coordinate */
  final def _1: Double = x

  /** @return the Y coordinate */
  final def _2: Double = y

  /** @return the Z coordinate */
  final def _3: Double = z

  /** @return the norm ("length") of this vector. */
  def norm: Double = math.sqrt(squaredNorm)

  /** @return the squared norm of this vector. Faster than [[norm]]. */
  def squaredNorm: Double

  /** @return a normalized (i.e. divided so that its norm becomes 1) copy */
  def normalized: Vec3d

  /** @return the opposite/negation of this vector */
  def unary_-(): Vec3d

  /** @return a copy of this vector added to the other one */
  def +(v: Vec3d): Vec3d

  /** @return a copy of this vector minus the other one */
  def -(v: Vec3d): Vec3d

  /** @return a copy of this vector scaled (multiplied) by the given number */
  def *(k: Double): Vec3d

  /** @return a copy of this vector scaled (multiplied) by the given number */
  def *(k: Int): Vec3d

  /** @return the matrix multiplication of this row-vector by the given matrix */
  def *(m: Matrix3d): Vec3d

  /** @return a copy of this vector scaled (element-wise multiplied) by the given vector */
  def scaled(by: Vec3d): Vec3d

  /** @return the outer product of this column-vector by the given row-vector */
  def outer(v: Vec3d): Matrix3d

  /** @return the dot product between this vector and v */
  def dot(v: Vec3d): Double

  /** @return the cross product between this vector and v */
  def cross(v: Vec3d): Vec3d

  /** @return the norm of `(this vector) - to` */
  def distance(to: Vec3d): Double = math.sqrt(squaredDistance(to))

  /** @return the squared norm of `(this vector) - to`. Faster than [[distance()]] */
  def squaredDistance(to: Vec3d): Double

  /** @return the angle between this vector and another one, in radians */
  def angle(v: Vec3d): Double = math.acos((this dot v) / (norm*v.norm))

  /**
   * Calculates the signed angle between this vector and another one, using `planeNormal` as the
   * right-handed orientation reference.
   *
   * @param v the other vector
   * @param planeNormal a unit (normalized) vector orthogonal to the plane `Vect(this, v)`.
   * @return the signed angle (between -pi and +pi radians) between this vector and the vector v
   */
  def signedAngle(v: Vec3d, planeNormal: Vec3d): Double = math.atan2((this cross v) dot planeNormal, this dot v)

  /** @return the orthogonal projection of this vector on the given vector */
  def projection(on: Vec3d): Vec3d = ((this dot on) / on.squaredNorm) * this

  /** @return the difference between this vector and its orthogonal projection. */
  def rejection(to: Vec3d): Vec3d = this-projection(to)

  /** @return the vector that is orthogonal to this vector and passes by the point given by the
   *          other vector. This is actually [[rejection()]] applied the other way around. */
  def orthogonalBy(by: Vec3d): Vec3d = by.rejection(this)

  /** @return the orthogonal distance to the given vector */
  def orthogonalDistance(to: Vec3d): Double = (this-projection(to)).norm

  /** @return the squared orthogonal distance to the given vector. Faster than [[orthogonalDistance()]] */
  def squaredOrthDist(to: Vec3d): Double = (this-projection(to)).squaredNorm

  /** @return a mutable copy of this vector */
  def mutableCopy: MutableVec3d

  /** @return a copy of this vector */
  def copy: Vec3d = mutableCopy

  /** @return a diagonal matrix whose elements are the vectors' coordinates */
  def toDiagonalMatrix = new DiagonalMatrix(x,y,z)

  override def toString: String = s"Vec3d($x, $y, $z)"

  override final def clone(): Vec3d = copy
}
object Vec3d {
  final val Zero = Vec3d(0,0,0)
  final val PositiveX = Vec3d(1,0,0); final val NegativeX = Vec3d(-1,0,0)
  final val PositiveY = Vec3d(0,1,0); final val NegativeY = Vec3d(0,-1,0)
  final val PositiveZ = Vec3d(0,0,1); final val NegativeZ = Vec3d(0,0,-1)

  def Vec3d(x: Double, y: Double, z: Double) = new MutableVec3d(x, y, z)

  def fill(coord: Double) = new MutableVec3d(coord, coord, coord)

  def unapply(v: Vec3d): Option[(Double, Double, Double)] = Some(v.x, v.y, v.z)

  /**
   * Calculates the mixt product of the three given vector: `(u cross v) dot w`.
   * The mixt product isn't commutative, i.e. the result depends on the order of u,v,w.
   * @return the mixt product
   */
  def mixtProduct(u: Vec3d, v: Vec3d, w: Vec3d): Double = {
    val crossX = u.y*v.z - u.z*v.y
    val crossY = u.z*v.x - u.x*v.z
    val crossZ = u.x*v.y - u.y*v.x
    w.x*crossX + w.y*crossY + w.z*crossZ
  }
}
