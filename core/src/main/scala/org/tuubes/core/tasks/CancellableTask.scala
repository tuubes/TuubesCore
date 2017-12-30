package org.tuubes.core.tasks

/**
 * @author TheElectronWill
 */
trait CancellableTask {
	/** @return true if this task is done (completed, failed or cancelled) */
	def isDone: Boolean

	/** Tries to cancel this task */
	def cancel(): Unit

	/** Cancels this task immediately. If the task is running, its thread is interrupted. */
	def forceCancel(): Unit
}