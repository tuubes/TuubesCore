package org.mcphoton.event

import org.mcphoton.impl.runtime.ExecutionGroup

/**
 * @author TheElectronWill
 */
trait EventListener[E <: Event, I] {
	def onEvent(evt: E)(implicit callerGroup: ExecutionGroup, i: I): Unit
}