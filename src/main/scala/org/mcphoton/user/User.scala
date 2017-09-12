package org.mcphoton.user

import java.util.UUID

import org.mcphoton.world.Location

/**
 * @author TheElectronWill
 */
trait User {
	def name: String
	def accountId: UUID
	def location: Location
	def isOnline: Boolean
}