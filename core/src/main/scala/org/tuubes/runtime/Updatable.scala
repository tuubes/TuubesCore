package org.tuubes.runtime

import java.util.concurrent.atomic.AtomicReference

import org.tuubes.runtime.UpdatableStatus._

/**
 * @author TheElectronWill
 */
trait Updatable {
	private[tuubes] val _status = new AtomicReference[UpdatableStatus](CREATED)

	@volatile protected[tuubes] var execGroup: ExecutionGroup = _
	/**
	 * The previous ExecutionGroup of this Updatable. This field is set when the Updatable is
	 * moved to another group.
	 */
	@volatile private[tuubes] var oldGroup = new AtomicReference[ExecutionGroup]

	private[tuubes] def checkOldGroup(callerGroup: ExecutionGroup): Boolean = {
		oldGroup.compareAndSet(callerGroup, null)
	}

	/**
	 * Performs an update.
	 *
	 * @param dt the elapsed time, in seconds, since the last execution of the update loop
	 */
	def update(dt: Double): Unit

	/**
	 * Destroys this updatable and release all associated resources.
	 */
	final def destroy(): Boolean = {
		val success = _status.compareAndSet(VALID, DESTROYED)
		if (success) {
			destroyImpl()
		}
		success
	}

	protected def destroyImpl(): Unit

	/**
	 * Checks if this Updatable is valid, ie if has been assigned to an ExecutionGroup and not
	 * destroyed yet. An invalid Updatable should not be updated nor used.
	 *
	 * @return true if this updatable is currently valid
	 */
	def isValid: Boolean = _status.get() == VALID

	/**
	 * Sends the changes to the clients.
	 */
	@throws[java.io.IOException]
	def sendUpdates(): Unit
}