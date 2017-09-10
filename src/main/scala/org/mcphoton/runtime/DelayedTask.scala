package org.mcphoton.runtime

import java.util.concurrent.TimeUnit

/**
 * @author TheElectronWill
 */
trait DelayedTask extends CancellableTask {
	def getDelay(unit: TimeUnit): Long
}