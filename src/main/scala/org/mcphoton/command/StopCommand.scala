package org.mcphoton.command

import org.mcphoton.messaging.Messageable
import org.mcphoton.runtime.ExecutionGroup
import org.mcphoton.server.PhotonServer
import org.mcphoton.world.World

/**
 * @author TheElectronWill
 */
class StopCommand extends Command("stop") {
	override def execute(sender: Messageable, args: Seq[String])
						(implicit w: World, exgroup: ExecutionGroup): Unit = {
		PhotonServer.shutdown()
	}
}
