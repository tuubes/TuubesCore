package org.tuubes.runtime

import java.util.concurrent.Executor

/**
 * A singleton Executor that runs every task directly in the caller thread.
 *
 * @author TheElectronWill
 */
object DummyExecutor extends Executor {
	override def execute(command: Runnable): Unit = command.run()
}