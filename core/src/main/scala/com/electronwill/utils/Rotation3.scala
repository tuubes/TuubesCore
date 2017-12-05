package com.electronwill.utils

import scala.math.{toDegrees => deg}

/**
 * An immutable class representing three rotations in the space: yaw, pitch and roll.
 * The angles are in radians.
 *
 * @author TheElectronWill
 */
final class Rotation3(y: Float, private[this] val p: Float, r: Float) {
	require(p <= -PiHalf && p >= PiHalf, "Pitch must be in interval [-pi/2, +pi/2]")

	/** The yaw, between 0 and 2pi radians */
	val yaw: Float = normalize(y, 0, Pi2)

	/** The roll, between 0 and 2pi radians */
	val roll: Float = normalize(r, 0, Pi2)

	/** The pitch, between -pi/2 and +pi/2 */
	def pitch: Float = p

	def +(a: Rotation3) = new Rotation3(yaw + a.yaw, pitch + a.pitch, roll + a.roll)

	def -(a: Rotation3) = new Rotation3(yaw - a.yaw, pitch - a.pitch, roll - a.roll)

	def ~=(a: Rotation3, tolerance: Float): Boolean = {
		math.abs(yaw - a.yaw) <= tolerance &&
			math.abs(yaw - a.yaw) <= tolerance &&
			math.abs(roll - a.roll) <= tolerance
	}

	def toDegrees: (Float, Float, Float) = (deg(yaw).toFloat, deg(pitch).toFloat, deg(roll).toFloat)
}

object Rotation3 {
	/** Implicit conversion from Rotation3 to Rotation2 by removing the roll */
	implicit def toRotation2(r3: Rotation3): Rotation2 = new Rotation2(r3.yaw, r3.pitch)

	final val Zero = new Rotation3(0, 0, 0)
}