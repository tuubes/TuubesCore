package com.electronwill.collections

/**
 * == Overview ==
 * An Index is a collection that associates integers ("id") to values ("elements"). Ids are
 * automatically assigned to the new elements, you can't chose them. Furthermore, no distinction
 * is made between null and inexistant elements. Thanks to these restrictions, an Index is often
 * faster and more efficient than an equivalent Map.
 *
 * @author TheElectronWill
 */
abstract class Index[A] extends Iterable[(Int, A)] with Compactable {

  /**
	 * Adds an element to the index and returns its ID.
	 *
	 * @param element the element
	 * @return the id associated to the element
	 */
  def +=(element: A): Int

  /**
   * Constructs an element with its ID and adds it to the index.
   *
   * @param f the function that creates an element with an ID
   * @return the the newly created element
   */
  def +=(f: Int => A): A

  /**
	 * Removes and returns an element from the index.
	 *
	 * @param id the id of the element to remove
	 * @return an Option that contains the removed element, or None if there was no element
	 *         associated to the given id.
	 */
  def -=(id: Int): Option[A]

  /**
	 * Removes an element from the index if it is associated with the specified value.
	 *
	 * @param id            the id of the element to remove
	 * @param expectedValue the value expected to be currently associated with the id
	 * @return true if it has been removed, false otherwise
	 */
  def -=(id: Int, expectedValue: A): Boolean

  /**
	 * Gets an element from the index. May return null of throw an exception if not found.
	 *
	 * @param id the element's id
	 * @return the element at the given id if it exists, or null, or throws an exception
	 */
  def apply(id: Int): A

  /**
   * Gets an element from the index.
   *
   * @param id the element's id
   * @return an Option that contains the element, or None if there is no element associated to
   *         the given id.
   */
  def get(id: Int): Option[A] = Option(getOrNull(id))

  /**
	 * Gets an element from the index, or null if not found.
	 *
	 * @param id the element's id
	 * @return the element, or null if there is no element associated to the given id.
	 */
  def getOrNull(id: Int): A

  /**
   * Updates the element associated with a given id.
   *
   * @param id      the id
   * @param element the new element to associate with the given id
   */
  def update(id: Int, element: A): Unit

  /**
	 * Removes an element from the index.
	 *
	 * @param id the id of the element to remove
	 */
  def remove(id: Int): Unit

  /**
	 * Creates an iterator over the Index's values.
	 *
	 * @return the new iterator
	 */
  def valuesIterator: Iterator[A]

  /**
	 * Creates an iterator over the Index's keys.
	 *
	 * @return the new iterator
	 */
  def keysIterator: Iterator[Int]

  /**
	 * Creates an iterator over all entries in this RecyclingIndex.
	 *
	 * @return the new iterator.
	 */
  def iterator: Iterator[(Int, A)]
}
