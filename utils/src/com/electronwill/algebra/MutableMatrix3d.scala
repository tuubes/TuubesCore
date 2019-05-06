package com.electronwill.algebra

final class MutableMatrix3d(var _11: Double, var _12: Double, var _13: Double,
                            var _21: Double, var _22: Double, var _23: Double,
                            var _31: Double, var _32: Double, var _33: Double)
    extends Matrix3d {

  override def transposed: MutableMatrix3d = {
    new MutableMatrix3d(_11, _21, _31, _12, _22, _32, _13, _23, _33)
  }

  override def +(m: Matrix3d): Matrix3d = {
    new MutableMatrix3d(_11+m._11, _12+m._12, _13+m._13,
                        _21+m._21, _22+m._22, _23+m._23,
                        _31+m._31, _32+m._32, _33+m._33)
  }

  override def -(m: Matrix3d): Matrix3d = {
    new MutableMatrix3d(_11-m._11, _12-m._12, _13-m._13,
                        _21-m._21, _22-m._22, _23-m._23,
                        _31-m._31, _32-m._32, _33-m._33)
  }

  override def scaled(by: Matrix3d): Matrix3d = by match {
    case DiagonalMatrix(x,y,z) =>
      new DiagonalMatrix(_11*x, _22*y, _33*z)
    case _ =>
      new MutableMatrix3d(_11*by._11, _12*by._12, _13*by._13,
                          _21*by._21, _22*by._22, _23*by._23,
                          _31*by._31, _32*by._32, _33*by._33)
  }

  override def unary_-(): MutableMatrix3d = {
    new MutableMatrix3d(-_11, -_12, -_13, -_21, -_22, -_23, -_31, -_32, -_33)
  }

  override def *(k: Double): MutableMatrix3d = {
    new MutableMatrix3d(k*_11, k*_12, k*_13, k*_21, k*_22, k*_23, k*_31, k*_32, k*_33)
  }

  override def *(v: Vec3d): Vec3d = {
    val x = _11*v.x + _12*v.y + _13*v.z
    val y = _21*v.x + _22*v.y + _23*v.z
    val z = _31*v.x + _32*v.y + _33*v.z
    Vec3d(x, y, z)
  }

  override def *(m: Matrix3d): MutableMatrix3d = m match {
    case DiagonalMatrix(x,y,z) =>
      new MutableMatrix3d(_11*x, _12*y, _13*z, _21*x, _22*y, _23*z, _31*x, _32*y, _33*z)
    case _ =>
      val m11 = m._11; val m12 = m._12; val m13 = m._13
      val m21 = m._21; val m22 = m._22; val m23 = m._23
      val m31 = m._31; val m32 = m._32; val m33 = m._33

      val res11 = _11*m11 + _12*m21 + _13*m31
      val res12 = _11*m12 + _12*m22 + _13*m32
      val res13 = _11*m13 + _12*m23 + _13*m33

      val res21 = _21*m11 + _22*m21 + _23*m31
      val res22 = _21*m12 + _22*m22 + _23*m32
      val res23 = _21*m13 + _22*m23 + _23*m33

      val res31 = _31*m11 + _32*m21 + _33*m31
      val res32 = _31*m12 + _32*m22 + _33*m32
      val res33 = _31*m13 + _32*m23 + _33*m33

      new MutableMatrix3d(res11, res12, res13, res21, res22, res23, res31, res32, res33)
  }

  override def mutableCopy: MutableMatrix3d = {
    new MutableMatrix3d(_11, _12, _13, _21, _22, _23, _31, _32, _33)
  }

  override def copy: Matrix3d = mutableCopy

  // === Mutations ===
  def +=(m: Matrix3d): Unit = {
    _11 += m._11
    _12 += m._12
    _13 += m._13
    _21 += m._21
    _22 += m._22
    _23 += m._23
    _31 += m._31
    _32 += m._32
    _33 += m._33
  }
  
  def -=(m: Matrix3d): Unit = {
    _11 -= m._11
    _12 -= m._12
    _13 -= m._13
    _21 -= m._21
    _22 -= m._22
    _23 -= m._23
    _31 -= m._31
    _32 -= m._32
    _33 -= m._33
  }

  def *=(k: Double): Unit = {
    _11 *= k
    _12 *= k
    _13 *= k
    _21 *= k
    _22 *= k
    _23 *= k
    _31 *= k
    _32 *= k
    _33 *= k
  }

  def scale(by: Matrix3d): Unit = {
    _11 *= by._11
    _12 *= by._12
    _13 *= by._13
    _21 *= by._21
    _22 *= by._22
    _23 *= by._23
    _31 *= by._31
    _32 *= by._32
    _33 *= by._33
  }

  def *=(m: Matrix3d): Unit = m match {
    case DiagonalMatrix(x,y,z) =>
      _11*=x; _21*=x; _31*=x
      _12*=y; _22*=y; _32*=y
      _13*=z; _23*=z; _33*=z
    case _ =>
      val a11 = _11; val a12 = _12; val a13 = _13
      val a21 = _21; val a22 = _22; val a23 = _23
      val a31 = _31; val a32 = _32; val a33 = _33

      val b11 = m._11; val b12 = m._12; val b13 = m._13
      val b21 = m._21; val b22 = m._22; val b23 = m._23
      val b31 = m._31; val b32 = m._32; val b33 = m._33

      _11 = a11*b11 + a12*b21 + a13*b31
      _12 = a11*b12 + a12*b22 + a13*b32
      _13 = a11*b13 + a12*b23 + a13*b33

      _21 = a21*b11 + a22*b21 + a23*b31
      _22 = a21*b12 + a22*b22 + a23*b32
      _23 = a21*b13 + a22*b23 + a23*b33

      _31 = a31*b11 + a32*b21 + a33*b31
      _32 = a31*b12 + a32*b22 + a33*b32
      _33 = a31*b13 + a32*b23 + a33*b33
  }
}
