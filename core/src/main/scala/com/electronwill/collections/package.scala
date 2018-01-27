package com.electronwill

import scala.reflect.ClassTag

/**
 * @author TheElectronWill
 */
package object collections {
	private[collections] def grow[T: ClassTag](array: Array[T], newLength: Int): Array[T] = {
		val newArray = new Array[T](newLength)
		System.arraycopy(array, 0, newArray, 0, array.length)
		newArray
	}
	private[collections] def shrink[T: ClassTag](array: Array[T], newLength: Int): Array[T] = {
		val newArray = new Array[T](newLength)
		System.arraycopy(array, 0, newArray, 0, newLength)
		newArray
	}
	private[collections] def growAmortize[T: ClassTag](array: Array[T], minLength: Int): Array[T] = {
		val l = array.length
		grow(array, Math.max(minLength, l + l >> 1))
	}
}