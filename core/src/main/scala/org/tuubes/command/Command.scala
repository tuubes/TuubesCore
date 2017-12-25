package org.tuubes.command

import org.tuubes.messaging.ChatMessageable
import org.tuubes.runtime.ExecutionGroup
import org.tuubes.world.World

/**
 * @author TheElectronWill
 */
abstract class Command(val name: String) {
	def execute(sender: ChatMessageable, args: Seq[String])
			   (implicit w: World, exgroup: ExecutionGroup): Unit
}