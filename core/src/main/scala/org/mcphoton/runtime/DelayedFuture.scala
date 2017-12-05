package org.mcphoton.runtime

import java.util.concurrent.{ScheduledFuture, TimeUnit}

/**
 * @author TheElectronWill
 */
final class DelayedFuture(f: ScheduledFuture[_ <: Any])
	extends CancellableFuture[ScheduledFuture[_ <: Any]](f) with DelayedTask {
	override def getDelay(unit: TimeUnit): Long = future.getDelay(unit)
}