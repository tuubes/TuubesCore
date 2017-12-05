package com.electronwill

/**
 * @author TheElectronWill
 */
package object utils {
	private[utils] final val Pi2: Float = math.Pi.toFloat * 2f
	private[utils] final val PiHalf: Float = math.Pi.toFloat * 0.5f

	private[utils] def normalize(a: Float, min: Float, max: Float): Float = {
		var result = a
		while (a < min) result += max
		while (a > max) result -= max
		result
	}
}