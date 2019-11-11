package com.electronwill.collection

import scala.reflect.ClassTag

/**
 * Non thread-safe implementation of [[Bag]].
 *
 * @author TheElectronWill
 */
final class SimpleBag[A >: Null <: AnyRef: ClassTag](initialCapacity: Int = 16) extends Bag[A] {
  private[this] var array = new Array[A](initialCapacity)
  private[this] var s: Int = 0

  override def size: Int = s
  override def apply(i: Int): A = array(i)
  override def remove(i: Int): Unit = {
    val last = array(s)
    array(i) = last
    array(s) = null
    s -= 1
  }
  override def -=(elem: A): SimpleBag.this.type = {
    var i = 0
    while (i < s && array(i) != elem) {
      i += 1
    }
    if (i != s) {
      remove(i)
    }
    this
  }
  override def +=(elem: A): this.type = {
    if (array.length == s) {
      array = grow(array, s + (s >> 1))
    }
    array(s) = elem
    s += 1
    this
  }
  override def ++=(arr: Array[A], offset: Int, length: Int): this.type = {
    val newS = s + length
    if (newS >= array.length) {
      array = grow(array, math.max(newS, s + (s >> 1)))
    }
    System.arraycopy(arr, offset, array, s, length)
    this
  }

  def ++=(bag: SimpleBag[A], offset: Int = 0): this.type = {
    bag.addTo(this, offset)
    this
  }

  private def addTo(to: SimpleBag[A], offset: Int): Unit = {
    to ++= (array, offset, s - offset)
  }

  override def indexOf(elem: A): Int = {
    var i = 0
    while (i < s && array(i) != elem) {
      i += 1
    }
    if (i == s) -1 else i
  }
  override def iterator: MutableIterator[A] = new MutableIterator[A] {
    private[this] var i = 0
    private[this] val l = s

    override def hasNext: Boolean = i < l
    override def next(): A = {
      val v = array(i)
      i += 1
      v
    }
    override def remove(): Unit = {
      SimpleBag.this.remove(i)
    }
    override def insert(elem: A): Unit = {
      SimpleBag.this.+=(elem)
    }
  }

  override def foreach[U](f: A => U): Unit = {
    val l = s
    var i = 0
    while (i < l) {
      f(array(i))
      i += 1
    }
  }

  override def compact(): Unit = {
    if (array.length > s) {
      array = shrink(array, s)
    }
  }
  override def clear(): Unit = {
    var i = 0
    while (i < s) {
      array(i) = null
      i += 1
    }
    s = 0
  }
}
