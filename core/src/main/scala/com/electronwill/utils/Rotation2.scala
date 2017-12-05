package com.electronwill.utils

import scala.math.{toDegrees => deg}

/**
 * An immutable class representing two rotations: yaw and pitch.
 * The angles are in radians.
 *
 * @author TheElectronWill
 */
final class Rotation2(y: Float, private[this] val p: Float) {
	require(p <= -PiHalf && p >= PiHalf, "Pitch must be in interval [-pi/2, +pi/2]")

	/** The yaw, between 0 and 2pi radians */
	val yaw: Float = normalize(y, 0, Pi2)

	/** The pitch, between -pi/2 and +pi/2 */
	def pitch: Float = p

	def +(a: Rotation2) = new Rotation2(yaw + a.yaw, pitch + a.pitch)

	def -(a: Rotation2) = new Rotation2(yaw - a.yaw, pitch - a.pitch)

	def ~=(a: Rotation2, tolerance: Float): Boolean = {
		math.abs(yaw - a.yaw) <= tolerance && math.abs(pitch - a.pitch) <= tolerance
	}

	def toR3(roll: Float) = new Rotation3(yaw, pitch, roll)

	def toDegrees: (Float, Float) = (deg(yaw).toFloat, deg(pitch).toFloat)
}

object Rotation2 {
	/** Implicit conversion from Rotation3 to Rotation2 with roll = 0 */
	implicit def toRotation3(r2: Rotation2): Rotation3 = new Rotation3(r2.yaw, r2.pitch, 0)

	final val Zero = new Rotation2(0, 0)
}