package org.tuubes.core.engine

import org.tuubes.core.blocks.Area

/**
 * @author TheElectronWill
 */
trait World extends Actor {
	def blocks: Area
}