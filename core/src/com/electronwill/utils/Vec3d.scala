package com.electronwill.utils

/**
 * An immutable vector with floating-point coordinates.
 *
 * @author TheElectronWill
 */
final class Vec3d(val x: Double, val y: Double, val z: Double) {
  def +(v: Vec3d) = new Vec3d(x + v.x, y + v.y, z + v.z)

  def -(v: Vec3d): Vec3d = new Vec3d(x - v.x, y - v.y, z - v.z)

  def unary_- : Vec3d = new Vec3d(-x, -y, -z)

  def *(k: Double): Vec3d = new Vec3d(x * k, y * k, z * k)

  def /(k: Double): Vec3d = this * (1.0 / k)

  def cross(v: Vec3d): Vec3d =
    new Vec3d(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

  def dot(v: Vec3d): Double = x * v.x + y * v.y + z * v.z

  def sqNorm: Double = x * x + y * y + z * z

  def norm: Double = math.sqrt(sqNorm)

  def normalize: Vec3d = this / norm

  def sqDist(v: Vec3d): Double = {
    val dx = v.x - x
    val dy = v.y - y
    val dz = v.z - z
    dx * dx + dy * dy + dz * dz
  }

  def dist(v: Vec3d): Double = math.sqrt(dist(v))

  def angle(v: Vec3i): Double = math.acos((this dot v) / (norm * v.norm))

  override def equals(a: Any): Boolean = a match {
    case v: Vec3d => v.x == x && v.y == y && v.z == z
    case v: Vec3i =>
      x.isValidInt && x.toInt == v.x &&
        y.isValidInt && y.toInt == v.y &&
        z.isValidInt && z.toInt == v.z
    case _ => false
  }

  def ~=(v: Vec3d, tolerance: Double): Boolean = {
    math.abs(x - v.x) <= tolerance &&
    math.abs(y - v.y) <= tolerance &&
    math.abs(z - v.z) <= tolerance
  }

  override def hashCode(): Int = {
    val hash = 31 * x.hashCode() + y.hashCode()
    31 * hash + z.hashCode()
  }

  def checkAll(f: Double => Boolean): Boolean = f(x) && f(y) && f(z)

  def checkEach(v: Vec3d, f: (Double, Double) => Boolean): Boolean = {
    f(x, v.x) && f(y, v.y) && f(z, v.z)
  }

  def between(lower: Vec3d, upper: Vec3d): Boolean = {
    x >= lower.x && x <= upper.x &&
    y >= lower.y && y <= upper.y &&
    z >= lower.z && z <= upper.z
  }
}

object Vec3d {

  /** Converts a Vec3d to a Vec3i */
  implicit def toVec3i(v: Vec3d): Vec3i =
    new Vec3i(v.x.toInt, v.y.toInt, v.z.toInt)

  def middle(a: Vec3d, b: Vec3d): Vec3d =
    new Vec3d((a.x + b.x) * 0.5, (a.y + b.y) * 0.5, (a.z + b.z) * 0.5)

  def max(a: Vec3d, b: Vec3d): Vec3d =
    new Vec3d(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z))

  def min(a: Vec3d, b: Vec3d): Vec3d =
    new Vec3d(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z))

  val Zero = new Vec3d(0, 0, 0)
  val UnitX = new Vec3d(1, 0, 0)
  val UnitY = new Vec3d(0, 1, 0)
  val UnitZ = new Vec3d(0, 0, 1)
}
