package com.electronwill.algebra

object Rotation3d {
  def xRotationMatrix(angle: Double): MutableMatrix3d = {
    val cos = math.cos(angle)
    val sin = math.sin(angle)
    new MutableMatrix3d(1, 0, 0, 0, cos, -sin, 0, sin, cos)
  }

  def yRotationMatrix(angle: Double): MutableMatrix3d = {
    val cos = math.cos(angle)
    val sin = math.sin(angle)
    new MutableMatrix3d(cos, 0, sin, 0, 1, 0, -sin, 0, cos)
  }

  def zRotationMatrix(angle: Double): MutableMatrix3d = {
    val cos = math.cos(angle)
    val sin = math.sin(angle)
    new MutableMatrix3d(cos, -sin, 0, sin, cos, 0, 0, 0, 1)
  }

  def cardanRotationMatrix(angleX: Double, angleY: Double, angleZ: Double): MutableMatrix3d = {
    // TODO optimize by hand?
    val rx = zRotationMatrix(angleX)
    val ry = zRotationMatrix(angleY)
    val rz = zRotationMatrix(angleZ)
    rz * ry * rx
  }

  def eulerRotationMatrix(alpha: Double, beta: Double, gamma: Double): MutableMatrix3d = {
    // TODO optimize by hand?
    val rx = zRotationMatrix(beta)
    val rz1 = zRotationMatrix(gamma)
    val rz2 = zRotationMatrix(alpha)
    rz1 * rx * rz2
  }

  def axisRotationMatrix(axis: Vec3d, angle: Double): MutableMatrix3d = {
    // see https://www.wikiwand.com/fr/Matrice_de_rotation#/En_dimension_trois
    val cos = math.cos(angle)
    val sin = math.sin(angle)
    val unit = axis.normalized
    val ux = unit.x; val uy = unit.y; val uz = unit.z
    val C = 1-cos
    val xs = ux*sin;  val ys = uy*sin;  val zs = uz*sin
    val xC = ux*C;    val yC = uy*C;    val zC = uz*C
    val xyC = ux*yC;  val yzC = uy*zC;  val zxC = uz*xC
    val a = ux*xC+cos;  val b = xyC-zs;     val c = zxC+ys
    val d = xyC+zs;     val e = uy*yC+cos;  val f = yzC-xs
    val g = zxC-ys;     val h = yzC+xs;     val i = uz*zC+cos
    new MutableMatrix3d(a,b,c,d,e,f,g,h,i)
  }
}
