package org.mcphoton.user

import java.util.UUID

import org.mcphoton.world.Location

/**
 * @author TheElectronWill
 */
private[user] final class OfflineUser(val name: String, val accountId: UUID, val location: Location)
	extends User {
	override def isOnline = false
}