package org.tuubes.user

import java.util.UUID

import org.tuubes.world.Location

/**
 * @author TheElectronWill
 */
trait User {
	def name: String
	def accountId: UUID
	def location: Location
	def isOnline: Boolean
}