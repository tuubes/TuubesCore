package org.mcphoton.event

import java.util.concurrent.atomic.AtomicBoolean

import scala.collection.mutable

/**
 * @author TheElectronWill
 */
final class ListenKey[E <: Event, I](private[event] val container: mutable.Set[EventListener[_, I]],
									 private[event] val listener: EventListener[E, I]) {
	def cancel(): Unit = {
		if (valid.compareAndSet(true, false)) {
			container.remove(listener)
		}
	}
	def isValid: Boolean = valid.get()
	private[this] val valid = new AtomicBoolean(true)
}