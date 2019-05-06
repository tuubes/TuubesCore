package com.electronwill.algebra

final class DiagonalMatrix(var x: Double, var y: Double, var z: Double) extends Matrix3d {
  override def _11: Double = x
  override def _12: Double = 0
  override def _13: Double = 0

  override def _21: Double = 0
  override def _22: Double = y
  override def _23: Double = 0

  override def _31: Double = 0
  override def _32: Double = 0
  override def _33: Double = z

  override def transposed: Matrix3d = this

  override def *(k: Double): DiagonalMatrix = new DiagonalMatrix(x*k, y*k, z*k)

  override def *(v: Vec3d): Vec3d = Vec3d(x*v.x, y*v.y, z*v.z)

  override def *(m: Matrix3d): Matrix3d = m match {
    case DiagonalMatrix(dx,dy,dz) => new DiagonalMatrix(x*dx, y*dy, z*dz)
    case _ => new MutableMatrix3d(x*m._11, x*m._12, x*m._13, y*m._21, y*m._22, y*m._23, z*m._31, z*m._32, z*m._33)
  }
  
  override def +(m: Matrix3d): Matrix3d = m match {
    case DiagonalMatrix(dx,dy,dz) => new DiagonalMatrix(x+dx, y+dy, z+dz)
    case _ => new MutableMatrix3d(x+m._11, x+m._12, x+m._13, y+m._21, y+m._22, y+m._23, z+m._31, z+m._32, z+m._33)
  }

  override def -(m: Matrix3d): Matrix3d = m match {
    case DiagonalMatrix(dx,dy,dz) => new DiagonalMatrix(x-dx, y-dy, z-dz)
    case _ => new MutableMatrix3d(x-m._11, x-m._12, x-m._13, y-m._21, y-m._22, y-m._23, z-m._31, z-m._32, z-m._33)
  }

  override def unary_-(): DiagonalMatrix = new DiagonalMatrix(-x, -y, -z)

  override def scaled(by: Matrix3d): DiagonalMatrix = new DiagonalMatrix(x*by._11, y*by._22, z*by._33)

  override def mutableCopy: MutableMatrix3d = new MutableMatrix3d(x,0,0, 0,y,0, 0,0,z)

  override def copy: DiagonalMatrix = new DiagonalMatrix(x,y,z)

  override def trace: Double = x+y+z

  override def det: Double = x*y*z

  override def toString: String = s"DiagonalMatrix($x, $y, $z)"

  def toVector = new MutableVec3d(x,y,z)

  def +=(diagonal: Vec3d): Unit = {
    x += diagonal.x
    y += diagonal.y
    z += diagonal.z
  }

  def -=(diagonal: Vec3d): Unit = {
    x -= diagonal.x
    y -= diagonal.y
    z -= diagonal.z
  }
  
  def *=(diagonal: Vec3d): Unit = {
    x *= diagonal.x
    y *= diagonal.y
    z *= diagonal.z
  }
}
object DiagonalMatrix {
  def unapply(d: DiagonalMatrix): Option[(Double, Double, Double)] = {
    Some(d.x, d.y, d.z)
  }
}
