package org.tuubes.core.engine

/**
 * Trait for updatables objects. Updates are based on the time elapsed since the last batch of
 * updates ("delta-time" based).
 *
 * @author TheElectronWill
 */
trait Updatable {
	def update(dt: Double): Unit
}