package org.mcphoton.entity.mobs

import org.mcphoton.entity.{Entity, MobType}

/**
 * @author TheElectronWill
 */
abstract class MobEntity(t: MobType) extends Entity(t) {
	protected[this] var headPitch: Float = 0
}