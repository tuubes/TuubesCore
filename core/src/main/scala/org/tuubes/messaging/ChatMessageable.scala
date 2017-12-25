package org.tuubes.messaging

/**
 * @author TheElectronWill
 */
trait ChatMessageable {
	def sendMessage(msg: ChatComponent): Unit
}