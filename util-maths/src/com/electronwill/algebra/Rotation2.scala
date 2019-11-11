package com.electronwill.util

import scala.language.implicitConversions
import scala.math.{toDegrees => deg, toRadians => rad}
import MathUtils._

/**
 * An immutable class representing three rotations in the space: yaw (x) and pitch (y).
 *
 * @param yaw   the X rotation, normally between 0 and 2pi radians
 * @param pitch the Y rotation, normally between 0 and 2pi radians
 * @author TheElectronWill
 */
final case class Rotation2(yaw: Float, pitch: Float) {
  def +(a: Rotation2) = Rotation2(yaw + a.yaw, pitch + a.pitch)

  def -(a: Rotation2) = Rotation2(yaw - a.yaw, pitch - a.pitch)

  def ~=(a: Rotation2, tolerance: Float): Boolean = {
    math.abs(yaw - a.yaw) <= tolerance &&
      math.abs(yaw - a.yaw) <= tolerance
  }

  /**
   * Converts the rotation to degrees.
   *
   * @return a new Rotation2 with values in degrees
   */
  def toDegrees: Rotation2 = Rotation2(deg(yaw).toFloat, deg(pitch).toFloat)

  /**
   * Creates a new Rotation2 by normalizing the values to the usual range, assuming they're radians.
   *
   * @return a normalized Rotation2
   */
  def normalized: Rotation2 = {
    val y = stepNormalize(yaw, 0, TWICE_PI, TWICE_PI)
    val p = stepNormalize(pitch, 0, TWICE_PI, TWICE_PI)
    Rotation2(y, p)
  }
}

object Rotation2 {
  /** The null (zero) rotation */
  final val ZERO = new Rotation2(0, 0)

  /**
   * Creates a new Rotation2 from degrees values.
   *
   * @param yaw   the yaw, in degrees
   * @param pitch the pitch, in degrees
   * @return a normalized Rotation2 with values in radians
   */
  def fromDegrees(yaw: Float, pitch: Float): Rotation2 = {
    val y = stepNormalize(rad(yaw).toFloat, 0, TWICE_PI, TWICE_PI)
    val p = stepNormalize(rad(pitch).toFloat, 0, TWICE_PI, TWICE_PI)
    Rotation2(y, p)
  }

  /**
   * Implicit conversion from Rotation2 to Rotation2 by removing the roll
   */
  implicit def toRotation3(r2: Rotation2): Rotation3 = Rotation3(r2.yaw, r2.pitch, 0f)
}


