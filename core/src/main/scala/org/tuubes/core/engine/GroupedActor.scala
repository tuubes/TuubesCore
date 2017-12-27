package org.tuubes.core.engine

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Abstract actor that belongs to an [[ExecutionGroup]].
 *
 * @author TheElectronWill
 */
abstract class GroupedActor extends Actor {
	private[this] var group: ExecutionGroup = _
	private[this] val msgBox = new ConcurrentLinkedQueue[ActorMessage]()
	override def !(msg: ActorMessage): Unit = {
		if (msg.isInstanceOf[CoreMessage] || filter(msg)) {
			msgBox.add(msg)
		}
	}
	override protected def onMessage(msg: ActorMessage): Unit = {
		msg match {
			case ChangeGroupMessage(newGroup) => group = newGroup
		}
	}
	private[core] def executionGroup = group
}