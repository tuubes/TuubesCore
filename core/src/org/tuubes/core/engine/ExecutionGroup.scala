package org.tuubes.core.engine

import java.lang.invoke.{MethodHandles, MethodType, SwitchPoint}
import java.util
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

  private val stats = new MovingStats(ExecutionGroup.NbMovingStats)
  private val actors = new SimpleBag[GroupedActor](256) // Bag for O(1) removal
  private var lastTime: Double = Double.NaN
  private var maxUpdateTime = 0L
  private var minUpdateTime = 0L
  private var increaseCount = 0

  private var continue = false
  private var forwardDestination: ExecutionGroup = _
  private val continueSwitch = new SwitchPoint() // SwitchPoint invalidated when the group is deleted
  private val addHandle = continueSwitch.guardWithTest(ExecutionGroup.normalHandle,
                                                        ExecutionGroup.forwarderHandle)

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
  private def optimizeLightGroup(): Unit = {
    // Group too lightweight => decrease its max and min or merge it with another group
    if (increaseCount > 0) {
      maxUpdateTime = (maxUpdateTime / ExecutionGroup.IncreaseFactor).toLong
      minUpdateTime = (minUpdateTime / ExecutionGroup.IncreaseFactor).toLong
      increaseCount -= 1
    } else {
      // If belowMinGroup is null, set it to this group and return
      // If belowMinGroup is already set to this group, simply return
      // If belowMinGroup is an other group, reset the variable to null and merge with the group
      val otherGroup = ExecutionGroup.belowMinGroup.getAndUpdate(v => if (v == null || v == this) this else null)
      if (otherGroup != null && otherGroup != this) {
        // -- Merge with this other group --
        // 1) Setup the forwarding to avoid losing actor (see issue #45):
        forwardDestination = otherGroup
        SwitchPoint.invalidateAll(Array(continueSwitch))
        // 2) Move all the actors to the other group:
        otherGroup.merge(actors, toAdd, toMerge)
        // 3) Stop this group:
        ExecutionGroup.delete(this)
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
      optimizeLightGroup()
    }

    reschedule(updateTime)
  }

  /**
   * Adds an actor to this group. The actor must be in [[Created]] or [[Moving]] state.
   *
   * @param actor the actor to add
   */
  def add(actor: GroupedActor): Unit = {
    addHandle.invokeExact(actor) // Call normalAdd until the SwitchPoint is invalidated, then call forwardAdd
  }

  // private[ExecutionGroup] makes the method "public" in the bytecode, allowing the
  // MethodHandles.Lookup  to find the methods while being executed in the companion object
  /** Adds an actor to this group */
  private[ExecutionGroup] def normalAdd(actor: GroupedActor): Unit = {
    assert(actor.state == Created || actor.state == Moving)
    actor.group = this
    toAdd.offer(actor)
  }

  /** Adds an actor to the forwardDestination group, because this group has been merged */
  private[ExecutionGroup] def forwardAdd(actor: GroupedActor): Unit = {
    forwardDestination.add(actor)
  }

  /**
   * Merge a group of actors into this group.
   *
   * @param actors     the actors of the group to merge into this group
   * @param addQueue   the addition queue of the group to merge
   * @param mergeQueue the merge queue of the group to merge
   */
  private def merge(actors: SimpleBag[GroupedActor],
                    addQueue: util.Queue[GroupedActor],
                    mergeQueue: util.Queue[SimpleBag[GroupedActor]]): Unit = {
    actors.foreach(a => {a.group = this; a.state = Moving})
    toMerge.offer(actors)
    toMerge.addAll(mergeQueue)
    toAdd.addAll(addQueue)
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
  //--- Global parameters ---
  val NbMovingStats: Int = ???
  val MaxUpdateTime0: Long = ???
  val MinUpdateTime0: Long = ???
  val IncreaseFactor: Double = ???
  val MaxIncreaseCount: Int = ???
  val MaxGroupCount: Int = ???

  //--- Groups management ---
  private final val groups = new RecyclingIndex[ExecutionGroup](MaxGroupCount)
  private final val belowMinGroup = new AtomicReference[ExecutionGroup]()
  private final val groupOrdering: Ordering[ExecutionGroup] = Ordering.by(_.stats.mean)

  /**
   * Creates a new group, if possible.
   *
   * @return the new group, or None if the maximum number of groups has been reached
   */
  def create(): Option[ExecutionGroup] = {
    groups.synchronized {
      if (groups.size < MaxGroupCount) {
        val newGroup = groups += (i => new ExecutionGroup(i))
        Some(newGroup)
      } else {
        None
      }
    }
  }

  /** Deletes a group */
  private[engine] def delete(group: ExecutionGroup): Unit = {
    groups.synchronized {
      groups -= group.id
    }
  }

  /**
   * Returns an existing group by its id.
   *
   * @param id the group's id
   * @return the group, or None if no group matches this id
   */
  def existing(id: Int): Option[ExecutionGroup] = {
    groups.synchronized {
      groups.get(id)
    }
  }

  /**
   * Returns one of the lightest groups, that is, a group whose updateTime is smaller than the others.
   *
   * @return one of the lightest group
   */
  def lightest(): ExecutionGroup = {
    val mini = belowMinGroup.get()
    if (mini != null) {
      mini
    } else {
      groups.synchronized {
        groups.valuesIterator.min(groupOrdering)
      }
    }
  }


  //--- Handles for the SwitchPoint ---
  private final val (normalHandle, forwarderHandle) = {
    val lookup = MethodHandles.lookup()
    val n = lookup.findVirtual(classOf[ExecutionGroup],
                                "normalAdd",
                                MethodType.methodType(classOf[Unit], classOf[GroupedActor]))
    val f = lookup.findVirtual(classOf[ExecutionGroup],
                                "forwardAdd",
                                MethodType.methodType(classOf[Unit], classOf[GroupedActor]))
    (n, f)
  }
}
