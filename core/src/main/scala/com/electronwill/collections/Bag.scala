package com.electronwill.collections

import scala.collection.mutable

/**
 * A resizeable collection whose remove(i) method moves the last element to index i, and is
 * therefore O(1).
 *
 * @author TheElectronWill
 */
abstract class Bag[A] extends mutable.Iterable[A] with Compactable {
  def apply(i: Int): A
  def remove(i: Int): Unit
  def -=(elem: A): this.type
  def +=(elem: A): this.type
  def ++=(array: Array[A], offset: Int, length: Int): this.type
  def clear(): Unit
  def contains(elem: A): Boolean = indexOf(elem) != -1
  def iterator: MutableIterator[A]
  def indexOf(elem: A): Int

  override final def hasDefiniteSize = true
}
