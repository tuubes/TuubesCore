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

	override def !(msg: ActorMessage): Unit = {
		if (filter(msg)) {
			msgBox.add(msg)
		}
	}

	override protected def onMessage(msg: ActorMessage): Unit = {
		msg match {
			case Terminate =>
				group = null
				state = Terminated
			case MoveToGroup(newGroup) =>
				group = newGroup
				state = Moving
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

	def start(execGroup: ExecutionGroup): Unit = {
		assert(state == Created)
		if (execGroup != null) {
			group = execGroup
			execGroup += this
		}
	}
}