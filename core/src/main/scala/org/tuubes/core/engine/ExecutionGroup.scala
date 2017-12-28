package org.tuubes.core.engine

import com.electronwill.collections.SimpleBag

/**
 * @author TheElectronWill
 */
final class ExecutionGroup extends Runnable {
	private val toUpdate = new SimpleBag[Updatable](256)
	private var lastTime: Double = Double.NaN

	override def run(): Unit = {
		if (lastTime == Double.NaN) {
			lastTime = System.nanoTime()
		} else {
			val time = System.nanoTime()
			val dt = time - lastTime
			lastTime = time

			for (updatable <- toUpdate) {
				updatable.update(dt)
			}
		}
	}
}