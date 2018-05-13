package org.tuubes.core.engine

import java.util.concurrent.ConcurrentLinkedQueue

import ActorState._
import org.tuubes.core.engine.messages.{MoveToGroup, Terminate}

/**
 * @author TheElectronWill
 */
abstract class LocalActor(override final val id: ActorId) extends Actor {
  private[this] val msgBox = new ConcurrentLinkedQueue[ActorMessage]

  private[this] var _state: ActorState = Created
  private[this] var _group: ExecutionGroup = _
  private[engine] var moveGroup: ExecutionGroup = _

  override def !(msg: ActorMessage)(implicit currentGroup: ExecutionGroup): Unit = {
    if (filter(msg)) {
      if (currentGroup eq group) {
        /* Fast path! Messages coming from the actor's group are processed immediately
				   without going through the queue. */
        onMessage(msg)
      } else {
        msgBox.add(msg)
      }
    }
  }

  override protected def onMessage(msg: ActorMessage): Unit = {
    msg match {
      case Terminate =>
        group = null
        state = Terminated
      case MoveToGroup(newGroup) =>
        moveGroup = newGroup
    }
  }

  final def processMessages(): Unit = {
    var msg = msgBox.poll()
    while (msg != null && state == Running) {
      onMessage(msg)
      msg = msgBox.poll()
    }
  }

  def update(dt: Double): Unit

  override protected def filter(msg: ActorMessage): Boolean = {
    state != Terminated
  }

  def state: ActorState = _state
  private[engine] def state_=(s: ActorState): Unit = _state = s

  def group: ExecutionGroup = _group
  private[engine] def group_=(g: ExecutionGroup): Unit = _group = g
}
