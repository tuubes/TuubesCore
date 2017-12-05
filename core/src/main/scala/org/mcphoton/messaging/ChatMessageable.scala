package org.mcphoton.messaging

/**
 * @author TheElectronWill
 */
trait ChatMessageable {
	def sendMessage(msg: ChatComponent): Unit
}