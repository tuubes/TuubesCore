package com.electronwill

import scala.reflect.ClassTag

/**
 * This package contains lightweight optimzied collections.
 *
 * @author TheElectronWill
 */
package object collection {
  private[collection] def grow[T: ClassTag](array: Array[T], newLength: Int): Array[T] = {
    val newArray = new Array[T](newLength)
    System.arraycopy(array, 0, newArray, 0, array.length)
    newArray
  }
  private[collection] def shrink[T: ClassTag](array: Array[T], newLength: Int): Array[T] = {
    val newArray = new Array[T](newLength)
    System.arraycopy(array, 0, newArray, 0, newLength)
    newArray
  }
  private[collection] def growAmortize[T: ClassTag](array: Array[T], minLength: Int): Array[T] = {
    val l = array.length
    grow(array, Math.max(minLength, l + l >> 1))
  }
}
