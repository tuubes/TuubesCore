package org.tuubes.command

import org.tuubes.messaging.ChatMessageable
import org.tuubes.runtime.ExecutionGroup
import org.tuubes.server.PhotonServer
import org.tuubes.world.World

/**
 * @author TheElectronWill
 */
class StopCommand extends Command("stop") {
	override def execute(sender: ChatMessageable, args: Seq[String])
						(implicit w: World, exgroup: ExecutionGroup): Unit = {
		PhotonServer.shutdown()
	}
}
