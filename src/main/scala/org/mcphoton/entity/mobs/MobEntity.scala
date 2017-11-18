package org.mcphoton.entity.mobs

import org.mcphoton.entity.{BasicEntity, MobType}

/**
 * @author TheElectronWill
 */
abstract class MobEntity(t: MobType) extends BasicEntity(t) {
	protected[this] var headPitch: Float = 0
}