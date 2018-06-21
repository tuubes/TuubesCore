package org.tuubes.core.engine

import org.tuubes.core.engine.messages.MoveToGroup

/**
 * @author TheElectronWill
 */
abstract class GroupedActor(id: ActorId = ActorId.next()) extends LocalActor(id) {
  private[this] var _group: ExecutionGroup = _
  private[engine] var moveGroup: ExecutionGroup = _

  override def !(msg: ActorMessage)(implicit currentGroup: ExecutionGroup): Unit = {
    if (filter(msg)) {
      if ((currentGroup eq group) && (currentGroup ne null)) {
        /* Fast path! Messages coming from the actor's group are processed immediately
				   without going through the queue. */
        onMessage(msg)
      } else {
        msgBox.add(msg)
      }
    }
  }

  protected override def onMessage(msg: ActorMessage): Unit = {
    super.onMessage(msg)
    msg match {
      case MoveToGroup(newGroup) => moveGroup = newGroup
    }
  }

  def group: ExecutionGroup = _group
  private[engine] def group_=(g: ExecutionGroup): Unit = _group = g
}
