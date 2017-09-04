package org.mcphoton.runtime

/**
 * @author TheElectronWill
 */
final class VolatileArray[A](size: Int) {
	private[this] var array = new Array[A](size)

	def update(idx: Int, element: A): Unit = {
		val arr = array // volatile read
		arr(idx) = element
		array = arr // volatile write
		// visibility of the new element is ensured by the volatile read/write
	}

	def get(idx: Int) = array(idx)

	def underlying: Array[A] = array
}