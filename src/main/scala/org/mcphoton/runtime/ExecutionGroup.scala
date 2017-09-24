package org.mcphoton.runtime

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue, Executor, TimeUnit}

import com.electronwill.collections.SimpleBag
import com.typesafe.scalalogging.Logger

/**
 * @author TheElectronWill
 */
final class ExecutionGroup private(private[this] val name: String) extends Executor {
	private[this] val logger = Logger("ExecutionGroup " + name)

	/** Update period, in milliseconds */
	private[this] val updatePeriod = 100

	/** Contains the task to execute on next update */
	private[this] val tasks = new ConcurrentLinkedQueue[Runnable]

	/** The size of the task queue */
	private[this] val addedTaskCount = new AtomicInteger(0)

	/** Contains the objects to update on each update */
	private[this] val updatables = new SimpleBag[Updatable] // NOT THREAD-SAFE

	/** Contains the objects to add to [[updatables]] on next update */
	private[this] val newUpdatables = new ConcurrentLinkedQueue[Updatable]

	/** The size of the updatable queue */
	private[this] val addedUpdatableCount = new AtomicInteger(0)

	@volatile private[this] val groupTask = TaskSystem.scheduleAtFixedRate(() => updateGroup(),
		0, updatePeriod, TimeUnit.MILLISECONDS)

	private[this] var lastUpdateTime: Double = _

	/**
	 * Executes a task on next update.
	 *
	 * @param task the task to execute
	 */
	override def execute(task: Runnable): Unit = {
		tasks.add(task)
		addedTaskCount.getAndIncrement()
	}

	/**
	 * Adds an Updatable to this group.
	 *
	 * @param updatable the updatable
	 */
	private[mcphoton] def add(updatable: Updatable): Unit = {
		newUpdatables.add(updatable)
		addedUpdatableCount.getAndIncrement()
	}

	/** Updates this group */
	private def updateGroup(): Unit = {
		executeTasks()
		executeUpdates()
		registerNewUpdatables()
	}

	/** Executes all the pending tasks */
	private def executeTasks(): Unit = {
		// Doesn't execute the tasks that are added during the loop
		val limit = addedTaskCount.get()
		var n = 1
		var task = tasks.poll()
		while (n < limit && task != null) {
			try {
				task.run()
			} catch {
				case e: Exception => logger.error(s"ERROR IN TASK $task", e)
			}
			task = tasks.poll()
			n += 1
		}
		addedTaskCount.getAndAdd(-limit)
	}

	/** Calls [[Updatable#update(double)]] on each Updatable of the group. */
	private def executeUpdates(): Unit = {
		val currentTime = System.nanoTime()
		val deltaTime = (currentTime - lastUpdateTime) * 1000000000 // nanos * 10^9 = seconds
		lastUpdateTime = currentTime

		val it = updatables.iterator()
		while (it.hasNext) {
			val updatable = it.next()
			if (updatable.isDestroyed) {
				it.remove()
			} else {
				try {
					updatable.update(deltaTime)
				}
			}
		}
	}

	/** Puts the updatables of [[newUpdatables]] into [[updatables]] */
	private def registerNewUpdatables(): Unit = {
		// Doesn't register the objects that are added during the loop
		val limit = addedUpdatableCount.get()
		var n = 1
		var updatable = newUpdatables.poll()
		while (n < limit && updatable != null) {
			if (!updatable.isDestroyed) {
				updatables.add(updatable)
			}
			updatable = newUpdatables.poll()
			n += 1
		}
		addedUpdatableCount.getAndAdd(-limit)
	}
}
object ExecutionGroup {
	private[this] val groupsMap = new ConcurrentHashMap[String, ExecutionGroup]

	def apply(name: String): ExecutionGroup = {
		groupsMap.computeIfAbsent(name, new ExecutionGroup(_))
	}

	def get(name: String): Option[ExecutionGroup] = Option(groupsMap.get(name))
}