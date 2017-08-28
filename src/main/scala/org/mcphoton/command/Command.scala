package org.mcphoton.command

import org.mcphoton.impl.runtime.ExecutionGroup
import org.mcphoton.world.World

/**
 * @author TheElectronWill
 */
abstract class Command(val name: String = s"") {
	def execute(args: Seq[String])(implicit w: World, exgroup: ExecutionGroup): Unit
}