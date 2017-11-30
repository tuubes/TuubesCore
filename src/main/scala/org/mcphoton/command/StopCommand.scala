package org.mcphoton.command

import org.mcphoton.messaging.ChatMessageable
import org.mcphoton.runtime.ExecutionGroup
import org.mcphoton.server.PhotonServer
import org.mcphoton.world.World

/**
 * @author TheElectronWill
 */
class StopCommand extends Command("stop") {
	override def execute(sender: ChatMessageable, args: Seq[String])
						(implicit w: World, exgroup: ExecutionGroup): Unit = {
		PhotonServer.shutdown()
	}
}
