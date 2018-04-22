package com.electronwill

/**
 * @author TheElectronWill
 */
package object utils {
	final val Pi2: Float = math.Pi.toFloat * 2f
	final val PiHalf: Float = math.Pi.toFloat * 0.5f
	final val InvPi2: Float = 1f / Pi2
	final val InvPiHalf: Float = 1f / PiHalf

	private[utils] def normalize(a: Float, min: Float, max: Float): Float = {
		var result = a
		while (a < min) result += max
		while (a > max) result -= max
		result
	}

	trait Tagged[U]
	type @@[+T, U] = T with Tagged[U]
}