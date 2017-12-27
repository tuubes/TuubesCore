package org.tuubes.core.engine

import scala.collection.mutable.ArrayBuffer

/**
 * @author TheElectronWill
 */
final class GameObject extends GroupedActor with Updatable {
	private val props = new PropertyStorage()
	private val behaviors = new ArrayBuffer[Behavior]

	def properties: PropertyStorage = props

	override protected def onMessage(msg: ActorMessage): Unit = {
		for (behavior <- behaviors) {
			behavior.onMessage(msg, this)
		}
	}
	override protected def filter(msg: ActorMessage): Boolean = true

	override def update(dt: Double): Unit = {
		// Updates each behavior
		for (behavior <- behaviors) {
			behavior.update(dt, this)
		}
		// Notifies each property change
		// TODO
	}
}