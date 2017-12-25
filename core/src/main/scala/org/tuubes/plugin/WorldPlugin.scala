package org.tuubes.plugin

import org.tuubes.world.World

/**
 * A WorldPlugin is a Plugin that is enabled in only one world.
 * <p>
 * Let W be an implementation of WorldPlugin. There may be one instance of W per world, and each
 * instance will report the world it is enabled in.
 *
 * @author TheElectronWill
 */
abstract class WorldPlugin(final val world: World) extends Plugin {
	override val worlds = Seq(world)

	/** Called when the plugin is enabled */
	def onEnable(): Unit

	/** Called when the plugin is disabled */
	def onDisable(): Unit
}