package com.electronwill.collection

import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.Test

import java.util.concurrent.atomic.AtomicReference
import scala.reflect.ClassTag

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

		bag+= "a" += "b" += "c" += "d"
		assertEquals(4, bag.size)

		val result = bagOf("d", "b", "c")
		val removed = bag -= "a"
		assertEquals(result, bag)

		val it = bag.iterator
		while (it.hasNext) {
			val next = it.next()
			println(s"it.next = $next")
		}
		assertEquals(0, bag.size)
		assert(bag.isEmpty)
	}

	private def bagOf[E >: Null <: AnyRef : ClassTag](elements: E*): Bag[E] = {
		val bag = new SimpleBag[E]
		elements.foreach(bag.+=)
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

		val lastRead = new AtomicReference[String]("none")
		val lastAdded: String = "i5000"
		val removeThread = new Thread(() => {
			val it = bag.iterator
			while (it.hasNext) {
				val next = it.next()
				assert(next != null)
				//println(s"remove $next")
			}
		})
		val readThread = new Thread(() => {
			val it = bag.iterator
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
					bag += str
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
