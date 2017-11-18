package org.mcphoton.world

import better.files.File
import com.electronwill.collections.ConcurrentRecyclingIndex
import org.mcphoton.command.CommandSystem
import org.mcphoton.entity.Entity
import org.mcphoton.event.EventSystem
import org.mcphoton.plugin.{Plugin, WorldPluginSystem}
import org.mcphoton.runtime.ExecutionGroup
import org.mcphoton.server.PhotonServer
import org.mcphoton.world.generation.SimpleHeightmapBasedGenerator

/**
 * @author TheElectronWill
 */
final class World(n: String, val worldType: WorldType) {
	private[this] var _name: String = n
	private[this] var _dir: File = PhotonServer.DirWorlds / n
	private[this] val entityIndex = new ConcurrentRecyclingIndex[Entity](200)
	private[this] val worldGroup = ExecutionGroup("world." + n)

	var chunkGenerator: ChunkGenerator = new SimpleHeightmapBasedGenerator(this)
	val chunkIO = new ChunkIO(this)
	val commandSystem = new CommandSystem
	val eventSystem = new EventSystem[World](this)
	val pluginSystem = new WorldPluginSystem[Plugin](this)

	def name: String = _name.synchronized(_name)
	def renameTo(n: String): Unit = {
		_name.synchronized {
			_name = n
			_dir = PhotonServer.DirWorlds / n
		}
	}
	def dir: File = _name.synchronized(_dir)

	def register(e: Entity): Unit = {
		val id = entityIndex += e
		e.init(id, this, worldGroup)
		worldGroup.add(e)
	}

	def unregister(e: Entity): Unit = {
		entityIndex -= e.entityId
	}
}