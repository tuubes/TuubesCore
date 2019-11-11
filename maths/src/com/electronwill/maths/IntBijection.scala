package com.electronwill.util

import com.electronwill.collection.ArrayMap

/**
 * @author TheElectronWill
 */
final class IntBijection[Tag](initialCapacity: Int) extends (Int => Int) {
  private val directMap = new ArrayMap[Int](initialCapacity, -1)
  private val inverseMap = new ArrayMap[Int](initialCapacity, -1)

  def +=(x: Int, y: Int): Unit = {
    directMap(x) = y
    inverseMap(y) = x
  }

  def -=(x: Int, y: Int): Unit = {
    directMap -= (x, y)
    inverseMap -= (y, x)
  }

  def -=(x: Int): Unit = {
    for (y <- directMap.remove(x)) {
      inverseMap.remove(y)
    }
  }

  override def apply(x: Int): Int = directMap(x)

  def applyDirect(x: Int): Int = directMap(x)

  def applyInverse(y: Int): Int = inverseMap(y)

  def direct: (Int => Int) = directMap.apply

  def inverse: (Int => Int) = inverseMap.apply
}
