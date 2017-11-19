package org.mcphoton.server

import better.files.File
import com.typesafe.scalalogging.StrictLogging
import org.mcphoton.GameRegistry
import org.mcphoton.command.{CommandSystem, StopCommand}
import org.mcphoton.entity.mobs.Player
import org.mcphoton.network.ProtocolLibAdapter
import org.mcphoton.world.{World, WorldType}

import scala.collection.mutable

/**
 * @author TheElectronWill
 */
object PhotonServer extends StrictLogging {
	// Constant infos
	final val Version: String = "0.5-alpha"

	// Directories
	final val DirMain = File(System.getProperty("user.dir"))
	final val DirConfig: File = DirMain / "config"
	final val DirPlugins: File = DirMain / "plugins"
	final val DirWorlds: File = DirMain / "worlds"
	final val Config: ServerConfig = new ServerConfig

	// Worlds
	private[this] val worldsNameMap = new mutable.AnyRefMap[String, World]

	def world(name: String): Option[World] = worldsNameMap.get(name)

	private[mcphoton] def registerWorld(w: World): Unit = worldsNameMap.put(w.name, w)
	private[mcphoton] def unregisterWorld(w: World): Unit = worldsNameMap.remove(w.name)

	// ProtocolLib
	private[this] var protocolLibAdapter: ProtocolLibAdapter = _

	// Players
	val onlinePlayers = new mutable.ArrayBuffer[Player]

	def main(args: Array[String]): Unit = {
		logger.info(s"Photon Server version $Version")
		loadDirs()

		logger.info("Loading the access controller")
		AccessController.load()

		logger.info("Starting the console thread")
		ConsoleInputThread.start()

		logger.info("Registering the standard types (blocks, items, ...)")
		GameRegistry.autoRegister()

		logger.info("Loading the worlds")
		loadWorlds()

		logger.info("Loading the config")
		Config.load()

		logger.info("Loading the Photon's commands")
		loadCommands()

		//TODO load plugins

		logger.info("Starting the TCP server")
		startTcp()

		logger.info(s"Done! You can connect on port ${Config.port}.")
	}

	private def loadDirs(): Unit = {
		Seq(DirMain, DirConfig, DirPlugins, DirWorlds)
		.foreach(_.createIfNotExists(asDirectory = true))
	}

	private def startTcp(): Unit = {
		protocolLibAdapter = new ProtocolLibAdapter(Config.port)
		protocolLibAdapter.start()
	}

	private def loadWorlds(): Unit = {
		for (dir <- DirWorlds.list if dir.isDirectory) {
			val world = new World(dir.name, WorldType.OVERWORLD)
			registerWorld(world)
		}
	}

	private def loadCommands(): Unit = {
		CommandSystem.global.register(new StopCommand)
	}
}