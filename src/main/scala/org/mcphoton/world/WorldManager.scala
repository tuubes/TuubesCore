package org.mcphoton.world

import java.util.concurrent.ConcurrentHashMap

/**
 * @author TheElectronWill
 */
object WorldManager {
	private[this] final val worlds = new ConcurrentHashMap[String, World]

	/** @return the world with that name if it exists, or None */
	def get(name: String): Option[World] = Option(worlds.get(name))

	private[mcphoton] def register(w: World): Unit = worlds.put(w.getName, w)

	private[mcphoton] def unregister(w: World): Unit = worlds.remove(w.getName, w)
}