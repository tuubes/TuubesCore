package com.electronwill.collection

import scala.reflect.ClassTag

/**
 * ==Overview==
 * An index that maps values to Int keys. The ConcurrentRecyclingIndex is based on an array,
 * therefore it is fast but requires positive (>= 0) keys. The key of the previously removed
 * elements are re-used for the new elements.
 * ConcurrentRecyclingIndex is a thread-safe version of [[RecyclingIndex]].
 *
 * ==About null values==
 * Null values are considered to be the same as "no value at all".
 *
 * ==Performance==
 * The `get` and `update` operations run in constant time. The `add` and `remove` operations run
 * in amortized constant time.
 *
 * @author TheElectronWill
 */
final class ConcurrentRecyclingIndex[A >: Null <: AnyRef: ClassTag](initialCapacity: Int,
                                                                    initialRecyclingCapacity: Int)
    extends Index[A] {

  def this(initialCapacity: Int = 16) = {
    this(initialCapacity, initialCapacity / 4)
  }

  /** Contains the elements of the RecyclingIndex. */
  @volatile
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
    elements.synchronized {
      elementCount += 1
      var elems = elements //volatile read; var because it may be replaced by a bigger array
      val id: Int =
        if (recycleCount == 0) {
          if (elems.length < elementCount) {
            elems = growAmortize(elems, elementCount)
          }
          elementCount
        } else {
          recycleCount -= 1
          idsToRecycle(recycleCount)
        }
      elems(id) = element
      elements = elems //volatile write
      id
    }
  }


  override def +=(f: Int => A): A = {
    elements.synchronized {
      elementCount += 1
      var elems = elements //volatile read; var because it may be replaced by a bigger array
      val id: Int =
        if (recycleCount == 0) {
          if (elems.length < elementCount) {
            elems = growAmortize(elems, elementCount)
          }
          elementCount
        } else {
          recycleCount -= 1
          idsToRecycle(recycleCount)
        }
      val elem = f(id)
      elems(id) = elem
      elements = elems //volatile write
      elem
    }
  }

  override def remove(id: Int): Unit = {
    elements.synchronized {
      val elems = elements //volatile read
      val element = elems(id)
      if (element ne null) {
        doRemove(id, elems)
      }
    }
  }

  override def -=(id: Int): Option[A] = {
    elements.synchronized {
      val elems = elements //volatile read
      val element = elems(id)
      if (element eq null) {
        None
      } else {
        doRemove(id, elems)
        Some(element)
      }
    }
  }

  override def -=(id: Int, expectedValue: A): Boolean = {
    elements.synchronized {
      val elems = elements //volatile read
      val element = elems(id)
      if (element == expectedValue && (element ne null)) {
        doRemove(id, elems)
        true
      } else {
        false
      }
    }
  }

  private def doRemove(id: Int, elems: Array[A]): Unit = {
    elementCount -= 1
    recycleCount += 1
    if (idsToRecycle.length < recycleCount) {
      idsToRecycle = growAmortize(idsToRecycle, recycleCount)
    }
    idsToRecycle(recycleCount - 1) = id
    elems(id) = null
    elements = elems //volatile write
  }

  override def apply(id: Int): A = {
    elements(id) //volatile read, exception if not found
  }

  override def getOrNull(id: Int): A = {
    val elems = elements //volatile read
    if (id >= 0 && id < elems.length) elems(id) else null
  }

  override def update(id: Int, element: A): Unit = {
    val elems = elements //volatile read
    elems(id) = element
    elements = elems //volatile write
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

  override def iterator: Iterator[(Int, A)] = new Iterator[(Int, A)] {
    // Iterates over (key,elem) - WEAKLY CONSISTENT
    private[this] val elems = elements //volatile read
    private[this] var id = 0
    private[this] var nextElement: A = _
    private def findNext(): Unit = {
      // Finds the next non-null element
      while (id < elems.length && (nextElement eq null)) {
        val v = elems(id)
        if (v ne null) {
          nextElement = v.asInstanceOf[A]
        }
        id += 1
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
}
