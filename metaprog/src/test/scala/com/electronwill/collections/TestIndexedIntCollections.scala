package com.electronwill.collections

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions._

/**
 * @author TheElectronWill
 */
class TestIndexedIntCollections {
	@Test
	def testBag(): Unit = test(new IntBag())

	@Test
	def testList(): Unit = test(new IntList())

	private def test(collection: IndexedIntCollection): Unit = {
		(0 to 25).foreach(collection.add)
		println(s"add -> $collection")
		assertEquals(26, collection.size())
		assert(!collection.isEmpty)

		val it = collection.iterator()
		while (it.hasNext) {
			val next = it.next()
			println(s"next: $next")
			it.remove()
			println(s"remove -> $collection")
		}
		assertEquals(0, collection.size())
		assert(collection.isEmpty)
	}
}
