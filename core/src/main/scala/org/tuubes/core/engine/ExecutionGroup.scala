package org.tuubes.core.engine

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{ConcurrentLinkedQueue, TimeUnit}

import com.electronwill.collections.{RecyclingIndex, SimpleBag}
import com.electronwill.utils.MovingStats
import org.tuubes.core.engine.ActorState._
import org.tuubes.core.tasks.TaskSystem

import scala.util.control.NonFatal

/**
 * Processes a group of [[GroupedActor]]s. Use the [[start()]] method to start an ExecutionGroup.
 * The group will then schedules itself automatically.
 *
 * @author TheElectronWill
 */
final class ExecutionGroup(private val id: Int) extends Runnable {
  private val toAdd = new ConcurrentLinkedQueue[GroupedActor]
  private val toMerge = new ConcurrentLinkedQueue[SimpleBag[GroupedActor]]

  private val actors = new SimpleBag[GroupedActor](256) // Bag for O(1) removal
  private var lastTime: Double = Double.NaN
  private var maxUpdateTime = 0L
  private var minUpdateTime = 0L
  private var increaseCount = 0
  private var continue = false

  private val stats = new MovingStats(ExecutionGroup.NbMovingStats)

  /** Accepts the actors that are waiting to be added and merged into this group */
  private def acceptNewActors(): Unit = {
    // Take the new individual actors
    var actor = toAdd.poll()
    while (actor != null) {
      actor.state = Running
      actors += actor
      actor = toAdd.poll()
    }

    // Merge the pending groups
    var bag = toMerge.poll()
    while (bag != null) {
      bag.foreach(_.state = Running)
      actors ++= bag // optimized add, see SimpleBag.scala
      bag = toMerge.poll()
    }
  }

  /** Updates all the group's actors */
  private def updateActors(): Unit = {
    // Run the updates
    val now = System.nanoTime()
    val dt = now - lastTime
    lastTime = now
    val it = actors.iterator
    while (it.hasNext) {
      val actor = it.next()
      try { // Resist to actors' errors
        actor.processMessages()
        if (actor.state == Terminated) { // Actor terminated because of a message
          it.remove()
        } else {
          actor.update(dt)
          if (actor.state == Terminated) { // Actor terminated during the update
            it.remove()
          } else { // Actor not terminated... but maybe moved?
            val moveGroup = actor.moveGroup
            if (moveGroup != null && moveGroup != this) { // Moved to another group
              actor.state = Moving
              actor.moveGroup = null
              it.remove()
              moveGroup.add(actor)
            }
          }
        }
      } catch {
        case NonFatal(e) => {
          //TODO log
          it.remove()
          actor.state = Terminated
        }
      }
    }
  }

  /** Optimizes this group when updateTime > maxUpdateTime */
  private def optimizeHeavyGroup(): Unit = {
    // Group too heavy => split it in 2 smaller groups or increase its max and min
    val created = ExecutionGroup.create()
    created match {
      case Some(group) => {
        // Move half of the actors to this new group
        // We can use the bag directly because the group hasn't started yet
        group.actors ++= (actors, actors.size / 2)

        // Start the new group
        group.start()
      }
      case None => {
        // Too many groups for now => try to increase maxUpdateTime
        if (increaseCount < ExecutionGroup.MaxIncreaseCount) {
          maxUpdateTime = (maxUpdateTime * ExecutionGroup.IncreaseFactor).toLong
          minUpdateTime = (minUpdateTime * ExecutionGroup.IncreaseFactor).toLong
          increaseCount += 1
        } // else: increase count too high => nothing can be done
      }
    }
  }

  /** Optimizes this group when updateTime < minUpdateTime */
  private def optimizeTinyGroup(): Unit = {
    // Group too small => decrease its max and min or merge it with another group
    if (increaseCount > 0) {
      maxUpdateTime = (maxUpdateTime / ExecutionGroup.IncreaseFactor).toLong
      minUpdateTime = (minUpdateTime / ExecutionGroup.IncreaseFactor).toLong
      increaseCount -= 1
    } else {
      val otherGroup = ExecutionGroup.belowMinGroup.getAndUpdate(v => if (v == null) this else v)
      if (otherGroup != null) {
        otherGroup.merge(actors)
        ExecutionGroup.delete(id)
        continue = false
      }
    }
  }

  /** Schedules the next execution of this group, based on `maxUpdateTime` and `updateTime` */
  private def reschedule(updateTime: Long): Unit = {
    if (continue) {
      val nextDelay = maxUpdateTime - updateTime
      TaskSystem.schedule(this, nextDelay, TimeUnit.NANOSECONDS)
      // NB: If nextDelay < 0 the ExecutorService uses a delay of 0
    }
  }

  /** Executes this group once */
  override def run(): Unit = {
    val t0 = System.nanoTime()

    acceptNewActors()
    updateActors()

    val t1 = System.nanoTime()
    val updateTime = t1 - t0
    stats.put(updateTime) // Update statistics

    // Split or merge group based on the statistics, in order to improve the overall performance and actor distribution
    val avgUpdateTime = stats.mean
    if (avgUpdateTime > maxUpdateTime) {
      optimizeHeavyGroup()
    } else if (avgUpdateTime < minUpdateTime) {
      optimizeTinyGroup()
    }

    reschedule(updateTime)
  }

  /**
   * Adds an actor to this group. The actor must be in [[Created]] or [[Moving]] state.
   *
   * @param actor the actor to add
   */
  def add(actor: GroupedActor): Unit = {
    assert(actor.state == Created || actor.state == Moving)
    actor.group = this
    toAdd.offer(actor)
  }

  /**
   * Merge a group of actors into this group.
   *
   * @param actors the actors to add to this group
   */
  def merge(actors: SimpleBag[GroupedActor]): Unit = {
    actors.foreach(a => {a.group = this; a.state = Moving})
    toMerge.offer(actors)
  }

  /**
   * Schedules the first run of this ExecutionGroup. The group will then re-schedule itself
   * automatically. A group must be started at most once. Attempting to start a group more than
   * once will throw an exception.
   */
  def start(): Unit = {
    assert(lastTime == Double.NaN)
    TaskSystem.execute(this)
  }
}

object ExecutionGroup {
  val NbMovingStats: Int = ???
  val MaxUpdateTime0: Long = ???
  val MinUpdateTime0: Long = ???
  val IncreaseFactor: Double = ???
  val MaxIncreaseCount: Int = ???
  val MaxGroupCount: Int = ???

  private val groups = new RecyclingIndex[ExecutionGroup](MaxGroupCount)
  private val belowMinGroup = new AtomicReference[ExecutionGroup]()

  def create(): Option[ExecutionGroup] = {
    groups.synchronized {
      if (groups.size < MaxGroupCount) {
        Some(groups += (i => new ExecutionGroup(i)))
      } else {
        None
      }
    }
  }

  def existing(id: Int): Option[ExecutionGroup] = {
    groups.synchronized {
      groups(id)
    }
  }

  private[engine] def delete(id: Int): Unit = {
    groups.synchronized {
      groups.remove(id)
    }
  }
}
