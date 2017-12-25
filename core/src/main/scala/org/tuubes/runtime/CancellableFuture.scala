package org.tuubes.runtime

import java.util.concurrent.Future

/**
 * @author TheElectronWill
 */
class CancellableFuture[T <: Future[_ <: Any]](protected[this] val future: T) extends CancellableTask {
	override def isDone: Boolean = future.isDone

	override def cancel(): Unit = future.cancel(false)

	override def forceCancel(): Unit = future.cancel(true)
}