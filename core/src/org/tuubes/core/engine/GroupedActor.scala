package org.tuubes.core.engine

import org.tuubes.core.engine.messages.MoveToGroup

/**
 * @author TheElectronWill
 */
abstract class GroupedActor extends LocalActor {
  private[engine] var group: ExecutionGroup = _
  private[engine] var moveGroup: ExecutionGroup = _

  override def !(msg: ActorMessage)(implicit currentGroup: ExecutionGroup): Unit = {
    if (filter(msg)) {
      if ((currentGroup eq group) && (currentGroup ne null)) {
        /* Fast path! Messages coming from the actor's group are processed immediately
				   without going through the queue. */
        onMessage(msg)
      } else {
        handleLater(msg)
      }
    }
  }

  protected override def onMessage(msg: ActorMessage): Unit = {
    super.onMessage(msg)
    msg match {
      case MoveToGroup(newGroup) => moveGroup = newGroup
    }
  }

  protected def handleLater(msg: ActorMessage): Unit = mailBox.add(msg)
}
