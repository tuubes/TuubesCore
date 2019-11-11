package com.electronwill.collection

import scala.collection.mutable

/**
 * A resizeable unordered collection whose remove(i) method moves the last element to index i and is
 * therefore O(1).
 *
 * @author TheElectronWill
 */
abstract class Bag[A] extends mutable.Iterable[A] with Compactable {
  /**
   * Gets an element from the bag.
   * @param i the element's index, the first element being at index 0
   * @return the element
   */
  def apply(i: Int): A

  /**
   * Removes an element from the bag.
   * @param i the element's index, the first element being at index 0
   */
  def remove(i: Int): Unit

  /**
   * Removes the first occurence of an element from the bag.
   * @param elem the element to remove
   * @return this bag
   */
  def -=(elem: A): this.type

  /**
   * Adds an element to the bag.
   * @param elem the element to add
   * @return this bag
   */
  def +=(elem: A): this.type

  /**
   * Adds multiple elements to the bag.
   * @param array the elements to add
   * @param offset first index in `array`
   * @param length number of elements to add
   * @return this bag
   */
  def ++=(array: Array[A], offset: Int, length: Int): this.type

  /**
   * Makes this bag empty.
   */
  def clear(): Unit

  /**
   * Checks if the bag contains an element.
   * @param elem the element to search
   * @return true if it contains at least one occurence of the given element, false otherwise
   */
  def contains(elem: A): Boolean = indexOf(elem) != -1

  /**
   * Gets the index of the first occurence of the given element.
   * @param elem the element to search
   * @return the index of the element, or -1 if not found
   */
  def indexOf(elem: A): Int

  /**
   * Gets an iterator over the elements of the bag.
   * @return an iterator over the bag's elements
   */
  def iterator: MutableIterator[A]

  override final def hasDefiniteSize = true
}
