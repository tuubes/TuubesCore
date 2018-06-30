package org.tuubes.core.plugins

import org.tuubes.core.worlds.LocalWorld

import scala.collection.mutable

/**
 * Manages the plugins of a specific world.
 *
 * @param world the managed world
 */
class WorldPluginManager(val world: LocalWorld) {
  protected val enabled = new mutable.AnyRefMap[String, Plugin]()

  def plugins: Iterable[Plugin] = enabled.values

  def plugin(name: String): Option[Plugin] = enabled.get(name)

  def enable(p: Plugin): Unit = {
    if (p.state != PluginState.DISABLED && !enabled.contains(p.name)) {
      p.onEnable(world)
      enabled(p.name) = p
    }
  }

  def disable(p: Plugin): Unit = {
    if (p.state != PluginState.DISABLED && enabled.remove(p.name).isDefined) {
      p.onDisable(world)
    }
  }
}
