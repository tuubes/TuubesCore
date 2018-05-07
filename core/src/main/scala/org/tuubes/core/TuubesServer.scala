package org.tuubes

import better.files.File
import org.slf4j.LoggerFactory

/**
 * @author TheElectronWill
 */
object TuubesServer {
	private[core] val logger = LoggerFactory.getLogger("TuubesCore")

	val Version: String = "0.6-alpha"

	val DirMain = File(System.getProperty("user.dir"))
	val DirConfig: File = DirMain / "config"
	val DirPlugins: File = DirMain / "plugins"
	val DirWorlds: File = DirMain / "worlds"
	val DirLogs: File = DirMain / "logs"

	def main(args: Array[String]): Unit = {

	}
}