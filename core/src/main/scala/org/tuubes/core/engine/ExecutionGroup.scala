package org.tuubes.core.engine

import java.util.concurrent.ConcurrentLinkedQueue

import com.electronwill.collections.SimpleBag
import ActorState._

import scala.util.control.NonFatal

/**
 * Processes a group of [[LocalActor]]s. An ExecutionGroup is intended to be scheduled as a task
 * by an ExecutorService.
 *
 * @author TheElectronWill
 */
final class ExecutionGroup extends Runnable {
	private val toUpdate = new SimpleBag[LocalActor](256)
	private var lastTime: Double = Double.NaN

	private val toAdd = new ConcurrentLinkedQueue[LocalActor]

	override def run(): Unit = {
		if (lastTime == Double.NaN) {
			lastTime = System.nanoTime()
		} else {
			val time = System.nanoTime()
			val dt = time - lastTime
			lastTime = time

			val it = toUpdate.iterator
			while (it.hasNext) {
				val actor = it.next()
				try {
					actor.processMessages()
					actor.state match {
						case Terminated =>
							it.remove()
						case Moving =>
							it.remove()
							actor.group += actor
					}
					actor.update(dt)
				} catch {
					case NonFatal(e) =>
					// TODO log
				}
			}
		}
	}

	def +=(actor: LocalActor): Unit = {
		assert(actor.state != Running && actor.group == this)
		toAdd.offer(actor)
		actor.state = ActorState.Running
	}
}