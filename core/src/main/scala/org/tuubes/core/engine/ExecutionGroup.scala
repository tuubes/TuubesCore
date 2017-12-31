package org.tuubes.core.engine

import java.util.concurrent.{ConcurrentLinkedQueue, TimeUnit}

import com.electronwill.collections.SimpleBag
import org.tuubes.core.engine.ActorState._
import org.tuubes.core.tasks.TaskSystem

import scala.util.control.NonFatal

/**
 * Processes a group of [[LocalActor]]s. An ExecutionGroup is intended to be scheduled as a task
 * by an ExecutorService.
 *
 * @author TheElectronWill
 */
final class ExecutionGroup extends Runnable {
	private val toAdd = new ConcurrentLinkedQueue[LocalActor]
	private val toUpdate = new SimpleBag[LocalActor](256)
	private var lastTime: Double = Double.NaN

	private val targetDelta: Long = TimeUnit.MILLISECONDS.toNanos(100)

	override def run(): Unit = {
		if (lastTime == Double.NaN) {
			lastTime = System.nanoTime()
		} else {
			val time = System.nanoTime()
			val dt = time - lastTime
			lastTime = time

			// Takes the new actors
			var polled = toAdd.poll()
			while (polled != null) {
				toUpdate += polled
				polled = toAdd.poll()
			}

			val it = toUpdate.iterator
			while (it.hasNext) {
				val actor = it.next()
				try {
					actor.processMessages()
					if (actor.state == Terminated) {
						it.remove()
					} else {
						actor.update(dt)
						if (actor.state == Terminated) {
							it.remove()
						} else {
							val moveGroup = actor.moveGroup
							if (moveGroup != null && moveGroup != this) { // Moved to another group
								actor.state = Moving
								actor.moveGroup = null
								it.remove()
								moveGroup += actor
							}
						}
					}
				} catch {
					case NonFatal(e) =>
						//TODO log
						it.remove()
						actor.state = Terminated
				}
			}

			// Schedules next run
			val duration = System.nanoTime() - time
			val nextDelay = targetDelta - duration
			TaskSystem.schedule(this, nextDelay, TimeUnit.NANOSECONDS)
		}
	}

	def +=(actor: LocalActor): Unit = {
		assert(actor.state == Created || actor.state == Moving)
		toAdd.offer(actor)
		actor.group = this
		actor.state = ActorState.Running
	}
	def start(): Unit = {
		assert(lastTime == Double.NaN)
		TaskSystem.execute(this)
	}
}