package com.electronwill.algebra

/**
 * A 3x3 floating-point matrix. This trait intentionally provides no mutation methods.
 */
trait Matrix3d extends java.lang.Cloneable {
  def _11: Double
  def _12: Double
  def _13: Double
  def _21: Double
  def _22: Double
  def _23: Double
  def _31: Double
  def _32: Double
  def _33: Double

  def row1 = Vec3d(_11, _12, _13)
  def row2 = Vec3d(_21, _22, _23)
  def row3 = Vec3d(_31, _32, _33)

  def column1 = Vec3d(_11, _21, _31)
  def column2 = Vec3d(_12, _22, _32)
  def column3 = Vec3d(_13, _23, _33)

  def trace: Double = _11 + _22 + _33

  def det: Double = {
    val a = _11; val b = _12; val c = _13
    val d = _21; val e = _22; val f = _23
    val g = _31; val h = _32; val i = _33
    a*e*i + b*f*g + c*d*h - c*e*g - b*d*i - a*f*h
  }

  def transposed: Matrix3d

  def +(m: Matrix3d): Matrix3d

  def -(m: Matrix3d): Matrix3d

  def unary_-(): Matrix3d

  def *(k: Double): Matrix3d

  def *(v: Vec3d): Vec3d

  def *(m: Matrix3d): Matrix3d

  def scaled(by: Matrix3d): Matrix3d

  def mutableCopy: MutableMatrix3d

  def copy: Matrix3d

  override def toString: String =
    s"""
       |Matrix3d(
       |$_11 ; $_12 ; $_13
       |$_21 ; $_22 ; $_23
       |$_31 ; $_32 ; $_33
       |)
     """.stripMargin

  override final def clone(): Matrix3d = copy
}
object Matrix3d {
  final val Zero: Matrix3d = new DiagonalMatrix(0,0,0)
  final val Identity: Matrix3d = new DiagonalMatrix(1,1,1)
  final val OnlyX: Matrix3d = new DiagonalMatrix(1,0,0)
  final val OnlyY: Matrix3d = new DiagonalMatrix(0,1,0)
  final val OnlyZ: Matrix3d = new DiagonalMatrix(0,0,1)
  final val FullOnes: Matrix3d = fill(1)

  def byRows(r1: Vec3d, r2: Vec3d, r3: Vec3d): MutableMatrix3d = {
    new MutableMatrix3d(r1.x, r1.y, r1.z, r2.x, r2.y, r2.z, r3.x, r3.y, r3.z)
  }

  def byRows(v: Array[Double]): MutableMatrix3d = {
    require(v.length == 9, s"A 3x3 matrix requires exactly 9 values, not ${v.length}.")
    new MutableMatrix3d(v(0), v(1), v(2), v(3), v(4), v(5), v(6), v(7), v(8))
  }

  def byRows(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double, g: Double, h: Double, i: Double): MutableMatrix3d = {
    new MutableMatrix3d(a, b, c, d, e, f, g, h, i)
  }

  def byColumns(l1: Vec3d, l2: Vec3d, l3: Vec3d): MutableMatrix3d = {
    new MutableMatrix3d(l1.x, l1.y, l1.z, l2.x, l2.y, l2.z, l3.x, l3.y, l3.z)
  }

  def byColumns(v: Array[Double]): MutableMatrix3d = {
    require(v.length == 9, s"A 3x3 matrix requires exactly 9 values, not ${v.length}.")
    new MutableMatrix3d(v(0), v(3), v(6), v(1), v(4), v(7), v(2), v(5), v(8))
  }

  def byColumns(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double, g: Double, h: Double, i: Double): MutableMatrix3d = {
    new MutableMatrix3d(a, d, g, b, e, h, c, f, i)
  }

  def diagonal(x: Double, y: Double, z: Double): DiagonalMatrix = new DiagonalMatrix(x,y,z)

  def fillDiagonal(value: Double): DiagonalMatrix = new DiagonalMatrix(value, value, value)

  def fill(value: Double): MutableMatrix3d = {
    new MutableMatrix3d(value, value, value, value, value, value, value, value, value)
  }
}
