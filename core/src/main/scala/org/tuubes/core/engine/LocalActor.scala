package org.tuubes.core.engine

import java.util.concurrent.ConcurrentLinkedQueue

import ActorState._
import org.tuubes.core.engine.messages.{MoveToGroup, Terminate}

/**
 * @author TheElectronWill
 */
abstract class LocalActor(override final val id: ActorId) extends Actor {
	private[this] val msgBox = new ConcurrentLinkedQueue[ActorMessage]

	var state: ActorState = Created
	var group: ExecutionGroup = _
	var moveGroup: ExecutionGroup = _

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

	}
}