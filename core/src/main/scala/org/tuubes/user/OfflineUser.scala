package org.tuubes.user

import java.util.UUID

import org.tuubes.world.Location

/**
 * @author TheElectronWill
 */
private[user] final class OfflineUser(val name: String, val accountId: UUID, val location: Location)
	extends User {
	override def isOnline = false
}