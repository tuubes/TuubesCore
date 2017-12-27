package org.tuubes.core.engine

import com.electronwill.collections.SimpleBag

/**
 * @author TheElectronWill
 */
final class ExecutionGroup extends Runnable {
	private[this] val toUpdate = new SimpleBag[Updatable](256)
	override def run(): Unit = {

	}
}