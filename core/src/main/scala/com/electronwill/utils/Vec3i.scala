package com.electronwill.utils

/**
 * An immutable vector with integer coordinates.
 *
 * @author TheElectronWill
 */
final class Vec3i(val x: Int, val y: Int, val z: Int) {
  def +(v: Vec3i) = new Vec3i(x + v.x, y + v.y, z + v.z)

  def -(v: Vec3i): Vec3i = new Vec3i(x - v.x, y - v.y, z - v.z)

  def unary_- : Vec3i = new Vec3i(-x, -y, -z)

  def *(k: Int): Vec3i = new Vec3i(x * k, y * k, z * k)

  def *(k: Double): Vec3d = new Vec3d(x * k, y * k, z * k)

  def /(k: Double): Vec3d = this * (1.0 / k)

  def cross(v: Vec3i): Vec3i =
    new Vec3i(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

  def dot(v: Vec3i): Int = x * v.x + y * v.y + z * v.z

  def sqNorm: Int = x * x + y * y + z * z

  def norm: Double = math.sqrt(sqNorm)

  def normalize: Vec3d = this / norm

  def sqDist(v: Vec3i): Int = {
    val dx = v.x - x
    val dy = v.y - y
    val dz = v.z - z
    dx * dx + dy * dy + dz * dz
  }

  def dist(v: Vec3i): Double = math.sqrt(dist(v))

  def angle(v: Vec3i): Double = math.acos((this dot v) / (norm * v.norm))

  override def equals(a: Any): Boolean = a match {
    case v: Vec3i => v.x == x && v.y == y && v.z == z
    case v: Vec3d =>
      v.x.isValidInt && v.x.toInt == x &&
        v.y.isValidInt && v.y.toInt == y &&
        v.z.isValidInt && v.z.toInt == z
    case _ => false
  }

  override def hashCode(): Int = {
    val hash = 31 * x + y
    31 * hash + z
  }

  def checkAll(f: Int => Boolean): Boolean = f(x) && f(y) && f(z)

  def checkEach(v: Vec3i, f: (Int, Int) => Boolean): Boolean = {
    f(x, v.x) && f(y, v.y) && f(z, v.z)
  }

  def between(lower: Vec3i, upper: Vec3i): Boolean = {
    x >= lower.x && x <= upper.x &&
    y >= lower.y && y <= upper.y &&
    z >= lower.z && z <= upper.z
  }
}

object Vec3i {

  /** Converts a Vec3i to a Vec3d */
  implicit def toVec3d(v: Vec3i): Vec3d = new Vec3d(v.x, v.y, v.z)

  def middle(a: Vec3i, b: Vec3i): Vec3d =
    new Vec3d((a.x + b.x) / 2d, (a.y + b.y) / 2d, (a.z + b.z) / 2d)

  def max(a: Vec3i, b: Vec3i): Vec3i =
    new Vec3i(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z))

  def min(a: Vec3i, b: Vec3i): Vec3i =
    new Vec3i(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z))

  val Zero = new Vec3i(0, 0, 0)
  val UnitX = new Vec3i(1, 0, 0)
  val UnitY = new Vec3i(0, 1, 0)
  val UnitZ = new Vec3i(0, 0, 1)
}
