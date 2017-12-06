package com.electronwill.collections

import scala.reflect.ClassTag

/**
 * @author TheElectronWill
 */
final class SimpleBag[A >: Null : ClassTag](initialCapacity: Int = 16) extends Bag[A] {
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
	override def iterator: Iterator[A] = new Iterator[A] {
		private[this] var i = 0

		override def hasNext: Boolean = i < s
		override def next(): A = {
			val v = array(i)
			i += 1
			v
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