package com.electronwill.algebra

final class MutableVec3d(private[this] var vx: Double,
                         private[this] var vy: Double,
                         private[this] var vz: Double)
    extends Vec3d {
  override def x: Double = vx

  override def y: Double = vy

  override def z: Double = vz

  override def squaredNorm: Double = vx * vx + vy * vy + vz * vz

  override def normalized: Vec3d = this * (1.0 / norm)

  override def unary_-(): Vec3d = new MutableVec3d(-vx, -vy, -vz)

  override def +(v: Vec3d): MutableVec3d = new MutableVec3d(vx + v.x, vy + v.y, vz + v.z)

  override def -(v: Vec3d): MutableVec3d = new MutableVec3d(vx - v.x, vy - v.y, vz - v.z)

  override def *(k: Double): MutableVec3d = new MutableVec3d(k*vx, k*vy, k*vz)

  override def *(k: Int): MutableVec3d = this * k.toDouble

  override def outer(v: Vec3d): Matrix3d = {
    val a = x*v.x; val b = x*v.y; val c = x*v.z
    val d = y*v.x; val e = y*v.y; val f = y*v.z
    val g = z*v.x; val h = z*v.y; val i = z*v.z
    new MutableMatrix3d(a,b,c,d,e,f,g,h,i)
  }

  override def *(m: Matrix3d): Vec3d = m match {
    case diag: DiagonalMatrix => diag * this
    case _ =>
      val a = vx * m._11 + m._21 * vy + m._31 * vz
      val b = vx * m._12 + m._22 * vy + m._32 * vz
      val c = vx * m._13 + m._23 * vy + m._33 * vz
      new MutableVec3d(a, b, c)
  }

  override def scaled(by: Vec3d): Vec3d = new MutableVec3d(vx * by.x, vy * by.y, vz * by.z)

  override def dot(v: Vec3d): Double = vx * v.x + vy * v.y + vz * v.z

  override def cross(v: Vec3d): Vec3d = {
    val a = vy*v.z - vz*v.y
    val b = vz*v.x - vx*v.z
    val c = vx*v.y - vy*v.x
    new MutableVec3d(a, b, c)
  }

  override def squaredDistance(to: Vec3d): Double = {
    val dx = to.x - vx
    val dy = to.y - vy
    val dz = to.z - vz
    dx * dx + dy * dy + dz * dz
  }

  override def mutableCopy = new MutableVec3d(vx, vy, vz)

  // overrides the return type to be more precise
  override def copy: MutableVec3d = mutableCopy

  // === Methods from java.lang.Object ===
  override def equals(obj: Any): Boolean = (obj.asInstanceOf[AnyRef] eq this) || (obj match {
    case v: Vec3d => vx == v.x && vy == v.y && vz == v.z
    case _ => false
  })

  override def hashCode: Int = {
    val hx = java.lang.Double.hashCode(vx)
    val hy = java.lang.Double.hashCode(vy)
    val hz = java.lang.Double.hashCode(vz)
    hy + 31 * (hz + 31 * hx)
  }

  override def clone: MutableVec3d = mutableCopy

  // === Mutations ===
  def /=(k: Double): Unit = this *= (1/k)

  def *=(k: Double): Unit = {
    vx *= k
    vy *= k
    vz *= k
  }

  def +=(v: Vec3d): Unit = {
    vx += v.x
    vy += v.y
    vz += v.z
  }

  def -=(v: Vec3d): Unit = {
    vx -= v.x
    vy -= v.y
    vz -= v.z
  }

  /**
   * Normalizes this vector, i.e. divide its components by its norm.
   *
   * @return this vector, modified
   */
  def normalize(): this.type = {
    val n = norm
    vx /= n
    vy /= n
    vz /= n
    this
  }

  /**
   * Scales this vector, i.e. multiplies each of its components by the corresponding one in the
   * given vector.
   *
   * @param by the vector to scale by
   * @return this vector, modified
   */
  def scale(by: Vec3d): this.type = {
    vx *= by.x
    vy *= by.y
    vz *= by.z
    this
  }

  /**
   * Projects this vector (orthogonal projection) on another vector.
   *
   * @param on the vector to project on
   * @return this vector, modified
   */
  def project(on: Vec3d): this.type = {
    this *= (this dot on) / on.squaredNorm
    this
  }

  // === Setters ===
  def x_=(x: Double): Unit = vx = x
  def y_=(y: Double): Unit = vy = y
  def z_=(z: Double): Unit = vz = z
}
