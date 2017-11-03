package org.mcphoton.command

import org.mcphoton.messaging.Messageable
import org.mcphoton.runtime.ExecutionGroup
import org.mcphoton.world.World

/**
 * @author TheElectronWill
 */
abstract class Command(val name: String) {
	def execute(sender: Messageable, args: Seq[String])
			   (implicit w: World, exgroup: ExecutionGroup): Unit
}