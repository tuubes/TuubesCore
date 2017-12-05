package com.electronwill.collections

import java.util.concurrent.atomic.AtomicReference

import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.Test

/**
 * @author TheElectronWill
 */
class TestBags {

	@Test
	def testSimpleBag(): Unit = {
		test(new SimpleBag[String])
	}

	@Test
	def testConcurrentBag(): Unit = {
		test(new ConcurrentBag[String])
		testMultiThread(new ConcurrentBag[String])
	}

	private def test(bag: Bag[String]): Unit = {
		assertEquals(0, bag.size)
		assert(bag.isEmpty)

		bag.add("a")
		bag.add("b")
		bag.add("c")
		bag.add("d")
		assertEquals(4, bag.size)

		val result = bagOf("d", "b", "c")
		val removed = bag.remove("a")
		assertTrue(removed)
		assertEquals(result, bag)

		val it = bag.iterator()
		while (it.hasNext) {
			val next = it.next()
			println(s"it.next = $next")
			assertTrue(result.contains(next))
			it.remove()
			println(s"remove() -> $bag")
		}
		assertEquals(0, bag.size)
		assert(bag.isEmpty)
	}

	private def bagOf[E](elements: E*): Bag[E] = {
		val bag = new SimpleBag[E]
		elements.foreach(bag.add)
		bag
	}

	private def testMultiThread(bag: Bag[String]): Unit = {
		val count = 200
		val insertionThreads = Seq(insertionThread("a", count, bag), insertionThread("b", count, bag), insertionThread("c", count, bag))
		runAll(insertionThreads)
		println(s"bag after parallel insertions: $bag")
		assertFalse(bag.isEmpty)
		assertEquals(3 * count, bag.size)
		checkInsertion(bag, count, "a", "b", "c")

		val lastRemoved = new AtomicReference[String]("none")
		val lastRead = new AtomicReference[String]("none")
		val lastAdded: String = "i5000"
		val removeThread = new Thread(() => {
			val it = bag.iterator()
			while (it.hasNext) {
				val next = it.next()
				assert(next != null)
				it.remove()
				//println(s"remove $next")
				lastRemoved.set(next)
			}
		})
		val readThread = new Thread(() => {
			val it = bag.iterator()
			while (it.hasNext) {
				val next = it.next()
				assert(next != null)
				lastRead.set(next)
				//println(s"read $next, size ${bag.size}")
			}
		})
		val addThread = insertionThread("i", 5000, bag)
		val miscThreads = Seq(addThread, removeThread, readThread)
		runAll(miscThreads)
		println(s"Size after parellel removals: ${bag.size}")
		println(s"Last removed: ${lastRemoved.get}")
		println(s"Last added: $lastAdded")
		println(s"Last read: ${lastRead.get}")

		bag.clear()
		assert(bag.isEmpty)
	}

	private def runAll(threads: Seq[Thread]): Unit = {
		threads.foreach(_.start)
		threads.foreach(_.join)
	}

	private def insertionThread(threadName: String, count: Int, bag: Bag[String]): Thread = {
		new Thread() {
			override def run(): Unit = {
				var i = 0
				while (i < count) {
					val str = s"$threadName$i"
					//println("add " + str)
					bag.add(str)
					i += 1
				}
			}
		}
	}

	private def checkInsertion(bag: Bag[String], count: Int, names: String*): Unit = {
		for (n <- names) {
			var i = 0
			while (i < count) {
				assert(bag.contains(s"$n$i"))
				i += 1
			}
		}
	}
}