package org.tuubes.plugin

import com.typesafe.scalalogging.Logger
import org.tuubes.plugin.PluginState._
import org.tuubes.world.World

/**
 * @author TheElectronWill
 */
final class WorldPluginSystem[P <: Plugin](val world: World) extends PluginSystem[P] {
	val logger = Logger("WorldPluginSystem(" + world + ")")

	override def enable(p: P): Unit = {
		if (p.state == DISABLED || plugins.contains(p.name)) return
		p match {
			case globalPlugin: GlobalPlugin =>
				if (p.state == LOADED) {
					PluginSystem.global.enable(globalPlugin)
				}
				globalPlugin.onEnable(world)
				globalPlugin._worlds :+ world

			case worldPlugin: WorldPlugin =>
				if (p.state == ENABLED) {
					logger warn s"Attempted to enable the WorldPlugin ${worldPlugin.name} in " +
						s"world $world but it is already enabled in world ${worldPlugin.world}"
				} else if (worldPlugin.world ne world) {
					logger warn s"Attempted to enable the WorldPlugin ${worldPlugin.name} in " +
						s"world $world but is has been constructed for world ${worldPlugin.world}"
				} else {
					worldPlugin.onEnable()
				}
		}
		plugins.put(p.name, p)
		p.state = ENABLED
	}

	override def disable(p: P): Unit = {
		if (p.state != DISABLED) {
			p match {
				case globalPlugin: GlobalPlugin =>
					globalPlugin.onDisable(world)
					globalPlugin._worlds -= world

				case worldPlugin: WorldPlugin =>
					worldPlugin.onDisable()
					worldPlugin.state = DISABLED
			}
			plugins.remove(p.name)
		}
	}
}
