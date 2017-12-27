package org.tuubes.core.engine

/**
 * @author TheElectronWill
 */
trait Behavior {
	def onMessage(msg: ActorMessage, receiver: GameObject): Unit
	def update(dt: Double, obj: GameObject): Unit
}
abstract class StatefulBehavior(protected[this] val go: GameObject) extends Behavior {
	override final def onMessage(msg: ActorMessage, receiver: GameObject): Unit = {
		if (receiver eq go) {
			onMessage(msg)
		}
	}
	override final def update(dt: Double, obj: GameObject): Unit = {
		if (obj eq go) {
			update(dt)
		}
	}
	def onMessage(msg: ActorMessage): Unit
	def update(dt: Double): Unit
}