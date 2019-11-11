package com.electronwill.collection

import scala.reflect.ClassTag

/**
 * Thread-safe implementation of [[Bag]] whose [[apply]] method doesn't use `synchronized` nor a lock.
 *
 * @author TheElectronWill
 */
final class ConcurrentBag[A >: Null <: AnyRef: ClassTag](initialCapacity: Int = 16) extends Bag[A] {
  /** Current bag size */
  @volatile private[this] var s = 0

  /** Elements storage */
  @volatile private[this] var array = new Array[A](initialCapacity)

  override def apply(i: Int): A = {
    if (i >= 0 && i < s) {
      array(i)
    } else {
      throw new IndexOutOfBoundsException("Invalid index: " + i)
    }
  }
  override def size: Int = s

  override def remove(i: Int): Unit = {
    this.synchronized {
      val length = s
      if (i >= 0 && i < length) {
        val updatedArray = array
        val newLength = length - 1
        updatedArray(i) = updatedArray(newLength)
        updatedArray(newLength) = null
        array = updatedArray
        s = newLength
      }
    }
  }
  override def +=(elem: A): ConcurrentBag.this.type = {
    this.synchronized {
      val length = s
      var updatedArray = array
      if (length == updatedArray.length) {
        updatedArray = grow(updatedArray, length + (length >> 1))
      }
      updatedArray(length) = elem
      array = updatedArray
      s = length + 1
    }
    this
  }
  override def ++=(arr: Array[A], offset: Int, length: Int): this.type = {
    this.synchronized {
      val newS = s + length
      if (newS >= array.length) {
        array = grow(array, math.max(newS, s + (s >> 1)))
      }
      System.arraycopy(arr, offset, array, s, length)
    }
    this
  }
  override def -=(elem: A): ConcurrentBag.this.type = {
    this.synchronized {
      val length = s
      val upToDateArray = array
      var i = 0
      while (i < length && upToDateArray(i) != elem) {
        i += 1
      }
      remove(i)
    }
    this
  }
  override def clear(): Unit = {
    this.synchronized {
      val length = s
      val updatedArray = array
      var i = 0
      while (i < length) {
        updatedArray(i) = null
        i += 1
      }
      array = updatedArray
      s = 0
    }
  }
  override def compact(): Unit = {
    if (s < array.length) {
      this.synchronized {
        array = shrink(array, s)
      }
    }
  }
  override def indexOf(elem: A): Int = {
    this.synchronized {
      var i = 0
      while (i < s && array(i) != elem) {
        i += 1
      }
      if (i == s) -1 else i
    }
  }
  override def iterator: MutableIterator[A] = new MutableIterator[A] {
    private[this] var i = 0
    private[this] val (arrayView: Array[A], sView: Int) = {
      var av: Array[A] = null
      var sv = 0
      do {
        av = array
        sv = s
      } while (array ne av)
      (av, sv)
    }
    override def hasNext: Boolean = {
      i < sView
    }
    override def next(): A = {
      val elem = arrayView(i)
      i += 1
      elem
    }
    override def remove(): Unit = {
      ConcurrentBag.this.remove(i)
    }
    override def insert(elem: A): Unit = {
      ConcurrentBag.this.+=(elem)
    }
  }
}
