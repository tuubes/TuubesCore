package org.tuubes.event

import org.tuubes.runtime.ExecutionGroup

/**
 * @author TheElectronWill
 */
trait EventListener[E <: Event, I] {
	def onEvent(evt: E)(implicit callerGroup: ExecutionGroup, i: I): Unit
}