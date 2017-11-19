package org.mcphoton.server

import better.files.File
import com.typesafe.scalalogging.StrictLogging
import org.mcphoton.GameRegistry
import org.mcphoton.command.{CommandSystem, StopCommand}
import org.mcphoton.entity.mobs.Player
import org.mcphoton.network.ProtocolLibAdapter
import org.mcphoton.plugin._
import org.mcphoton.world.{World, WorldType}

import scala.collection.mutable
import scala.util.{Failure, Success}

/**
 * @author TheElectronWill
 */
object PhotonServer extends StrictLogging {
	// Constant infos
	final val Version: String = "0.5-alpha"
	final val MinecraftVersion: String = "1.12"

	// Directories
	final val DirMain = File(System.getProperty("user.dir"))
	final val DirConfig: File = DirMain / "config"
	final val DirPlugins: File = DirMain / "plugins"
	final val DirWorlds: File = DirMain / "worlds"
	final val DirLogs: File = DirMain / "logs"
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

		logger.info("Loading the plugins")
		loadPlugins()

		logger.info("Starting the TCP server")
		startTcp()

		logger.info(s"Done! You can connect on port ${Config.port}.")
	}

	private def loadDirs(): Unit = {
		Seq(DirMain, DirConfig, DirPlugins, DirWorlds, DirLogs)
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
			//TODO load world config and spawn region
		}
	}

	private def loadCommands(): Unit = {
		CommandSystem.global.register(new StopCommand)
	}

	/** Loads the plugins that are in DirPlugins. Must be called after loadWorlds(). */
	private def loadPlugins(): Unit = {
		val graph = new DependencyGraph()
		for (file <- DirPlugins.children) {
			logger.debug(s"Analysing ${file.name}")
			val inspection = PluginInfos.inspect(file)
			inspection match {
				case Success(infos) =>
					logger.debug(s"Got infos $infos")
					graph.register(infos)
				case Failure(t) =>
					logger.debug(s"Got failure $t")
					logger.error(s"Error while loading $file", t)
			}
		}
		logger.debug("Building the dependencies graph")
		graph.build()
		logger.debug("Resolving the dependencies")
		val solution = graph.resolve()
		val nbError = solution.errors.size
		if (nbError > 0) {
			logger.warn(s"$nbError out of ${solution.resolvedItems.size} items couldn't be " +
				s"resolved. See below.")
			solution.errors.foreach(s => logger.warn("    " + s))
		}
		for (resolved <- solution.resolvedItems) {
			val pluginClass = resolved.loader.loadClass(resolved.infos.pluginClassName)
			if (classOf[GlobalPlugin].isAssignableFrom(pluginClass)) {
				val pluginInstance = pluginClass.newInstance().asInstanceOf[GlobalPlugin]
				GlobalPluginSystem.enable(pluginInstance)
				Config.spawnLocation.world.pluginSystem.enable(pluginInstance)
			} else if (classOf[WorldPlugin].isAssignableFrom(pluginClass)) {
				val pluginInstance = pluginClass.newInstance().asInstanceOf[WorldPlugin]
				Config.spawnLocation.world.pluginSystem.enable(pluginInstance)
			} else {
				logger.warn(s"Plugin $pluginClass is not a GlobalPlugin nor a WorldPlugin.")
			}
			//TODO per-world plugin list
		}
	}

	private[mcphoton] def shutdown(): Unit = {
		// TODO
		logger.warn("Shutdown is not properly implemented yet!")
		logger.info("Saving the worlds")
		for (world <- worldsNameMap.valuesIterator) {
			//TODO world.save()
		}
		logger.info("Stopping the TCP server")
		protocolLibAdapter.stop()

		logger.info("Shutdown.")
		System.exit(0)
	}

	Runtime.getRuntime.addShutdownHook(new Thread(() => LoggingService.close()))
}