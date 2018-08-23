package com.electronwill.utils

import scala.language.implicitConversions
import scala.math.{toDegrees => deg, toRadians => rad}
import MathUtils._

/**
 * An immutable class representing three rotations in the space: yaw (x), pitch (y) and roll (z).
 *
 * @param yaw   the X rotation, normally between 0 and 2pi radians
 * @param pitch the Y rotation, normally between 0 and 2pi radians
 * @param roll  the Z rotation, normally between -pi/2 and +pi/2 radians
 * @author TheElectronWill
 */
final case class Rotation3(yaw: Float, pitch: Float, roll: Float) {
  def +(a: Rotation3) = Rotation3(yaw + a.yaw, pitch + a.pitch, roll + a.roll)

  def -(a: Rotation3) = Rotation3(yaw - a.yaw, pitch - a.pitch, roll - a.roll)

  def ~=(a: Rotation3, tolerance: Float): Boolean = {
    math.abs(yaw - a.yaw) <= tolerance &&
      math.abs(yaw - a.yaw) <= tolerance &&
      math.abs(roll - a.roll) <= tolerance
  }

  /**
   * Converts the rotation to degrees.
   *
   * @return a new Rotation3 with values in degrees
   */
  def toDegrees: Rotation3 = Rotation3(deg(yaw).toFloat, deg(pitch).toFloat, deg(roll).toFloat)

  /**
   * Creates a new Rotation3 by normalizing the values to the usual range, assuming they're radians.
   *
   * @return a normalized Rotation3
   */
  def normalized: Rotation3 = {
    val y = stepNormalize(yaw, 0, TWICE_PI,TWICE_PI)
    val p = stepNormalize(pitch, 0, TWICE_PI,TWICE_PI)
    val r = stepNormalize(roll, NEG_HALF_PI, HALF_PI, HALF_PI)
    Rotation3(y, p, r)
  }
}

object Rotation3 {
  /** The null (zero) rotation */
  final val ZERO = new Rotation3(0, 0, 0)

  /**
   * Creates a new Rotation3 from degrees values.
   *
   * @param yaw   the yaw, in degrees
   * @param pitch the pitch, in degrees
   * @param roll  the roll, in degrees
   * @return a normalized Rotation3 with values in radians
   */
  def fromDegrees(yaw: Float, pitch: Float, roll: Float): Rotation3 = {
    val y = stepNormalize(rad(yaw).toFloat, 0, TWICE_PI,TWICE_PI)
    val p = stepNormalize(rad(pitch).toFloat, 0, TWICE_PI,TWICE_PI)
    val r = stepNormalize(rad(roll).toFloat, NEG_HALF_PI, HALF_PI, HALF_PI)
    Rotation3(y, p, r)
  }

  /**
   * Implicit conversion from Rotation3 to Rotation2 by removing the roll
   */
  implicit def toRotation2(r3: Rotation3): Rotation2 = Rotation2(r3.yaw, r3.pitch)
}
