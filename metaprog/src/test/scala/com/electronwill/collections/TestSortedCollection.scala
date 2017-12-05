package com.electronwill.collections

import java.util.Comparator

import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.Test

import scala.collection.mutable
import scala.util.Random

/**
 * @author TheElectronWill
 */
class TestSortedCollection {
	@Test
	def test(): Unit = {
		val comparator: Comparator[Integer] = Comparator.naturalOrder()
		val collection = new ConcurrentSortedCollection[Integer](comparator)
		val shuffled: mutable.Buffer[Int] = Random.shuffle((0 to 25).toBuffer)
		shuffled.foreach(collection.add(_))
		println(s"Sorted: $collection")
		assertEquals(26, collection.size())
	}

}
