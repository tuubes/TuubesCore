package org.tuubes.entity.mobs

import org.tuubes.entity.{BasicEntity, MobType}

/**
 * @author TheElectronWill
 */
abstract class MobEntity(t: MobType) extends BasicEntity(t) {
	protected[this] var headPitch: Float = 0
}