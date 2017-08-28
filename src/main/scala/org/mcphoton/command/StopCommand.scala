package org.mcphoton.command
import org.mcphoton.impl.runtime.ExecutionGroup
import org.mcphoton.world.World

/**
 * @author TheElectronWill
 */
class StopCommand extends Command("stop") {
	override def execute(args: Seq[String])(implicit w: World, exgroup: ExecutionGroup): Unit = {
		System.exit(0)
	}
}
