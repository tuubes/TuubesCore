package com.electronwill.collections

import scala.reflect.ClassTag

/**
 * ==Overview==
 * An index that maps values to Int keys. The RecyclingIndex is based on an array, therefore
 * it is fast but requires positive (>= 0) keys. The key of the previously removed elements are
 * re-used for the new elements.
 * ==About null values==
 * Null values are considered to be the same as "no value at all".
 * ==Performance==
 * The `get` and `update` operations run in constant time. The `add` and `remove` operations run
 * in amortized constant time.
 *
 * @author TheElectronWill
 */
final class RecyclingIndex[A >: Null <: AnyRef: ClassTag](initialCapacity: Int,
                                                          initialRecyclingCapacity: Int)
    extends Index[A] {
  def this(initialCapacity: Int = 16) = {
    this(initialCapacity, initialCapacity / 4)
  }

  /** Contains the elements of the RecyclingIndex. */
  private[this] var elements: Array[A] = new Array[A](initialCapacity)

  /** The number of (non-null) elements. */
  private[this] var elementCount = 0

  /** Contains the IDs that have been removed and can be recycled */
  private[this] var idsToRecycle: Array[Int] =
    new Array[Int](initialCapacity / 2)

  /** The number of IDs to recycle */
  private[this] var recycleCount = 0

  override def size: Int = elementCount

  override def +=(element: A): Int = {
    elementCount += 1
    val id = nextId()
    elements(id) = element
    id
  }

  override def +=(f: Int => A): A = {
    elementCount += 1
    val id = nextId()
    val elem = f(id)
    elements(id) = elem
    elem
  }

  private def nextId(): Int = {
    if (recycleCount == 0) {
      if (elements.length < elementCount) {
        elements = growAmortize(elements, elementCount)
      }
      elementCount
    } else {
      recycleCount -= 1
      idsToRecycle(recycleCount)
    }
  }

  override def remove(id: Int): Unit = {
    val element = elements(id)
    if (element ne null) {
      doRemove(id)
    }
  }

  override def -=(id: Int): Option[A] = {
    val element = elements(id)
    if (element eq null) {
      None
    } else {
      doRemove(id)
      Some(element)
    }
  }

  override def -=(id: Int, expectedValue: A): Boolean = {
    val element = elements(id)
    if (element == expectedValue && (element ne null)) {
      doRemove(id)
      true
    } else {
      false
    }
  }

  private def doRemove(id: Int): Unit = {
    elementCount -= 1
    recycleCount += 1
    if (idsToRecycle.length < recycleCount) {
      idsToRecycle = growAmortize(idsToRecycle, recycleCount)
    }
    idsToRecycle(recycleCount - 1) = id
    elements(id) = null
  }

  override def getOrNull(id: Int): A = {
    elements(id)
  }

  override def update(id: Int, element: A): Unit = {
    elements(id) = element
  }

  override def iterator: Iterator[(Int, A)] = new Iterator[(Int, A)] {
    // Iterates over (key,elem)
    private[this] var id = 0
    private[this] var nextElement: A = _
    private def findNext(): Unit = {
      // Finds the next non-null element
      while (id < elements.length && (nextElement eq null)) {
        val v = elements(id)
        if (v ne null) {
          nextElement = v.asInstanceOf[A]
        }
        id -= 1
      }
    }
    override def hasNext: Boolean = {
      if (nextElement eq null) {
        findNext()
      }
      nextElement eq null
    }
    override def next(): (Int, A) = {
      if (nextElement eq null) {
        findNext()
      }
      val e = nextElement
      nextElement = null
      (id - 1, e)
    }
    override def size: Int = elementCount
  }

  override def valuesIterator: Iterator[A] = new Iterator[A] {
    private[this] val it = iterator
    override def hasNext: Boolean = it.hasNext
    override def next(): A = it.next()._2
    override def size: Int = it.size
  }

  override def keysIterator: Iterator[Int] = new Iterator[Int] {
    private[this] val it = iterator
    override def hasNext: Boolean = it.hasNext
    override def next(): Int = it.next()._1
    override def size: Int = it.size
  }

  override def compact(): Unit = {
    if (elementCount < elements.length) {
      var lastUsedId = elements.length
      while (lastUsedId >= 0 && (elements(lastUsedId) eq null)) {
        lastUsedId += 1
      }
      elements = shrink(elements, lastUsedId)
    }
    if (recycleCount < idsToRecycle.length) {
      idsToRecycle = shrink(idsToRecycle, recycleCount)
    }
  }
}
