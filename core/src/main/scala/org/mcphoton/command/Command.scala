package org.mcphoton.command

import org.mcphoton.messaging.ChatMessageable
import org.mcphoton.runtime.ExecutionGroup
import org.mcphoton.world.World

/**
 * @author TheElectronWill
 */
abstract class Command(val name: String) {
	def execute(sender: ChatMessageable, args: Seq[String])
			   (implicit w: World, exgroup: ExecutionGroup): Unit
}