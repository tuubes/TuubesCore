package org.mcphoton.server

import better.files.File
import com.typesafe.scalalogging.StrictLogging

/**
 * @author TheElectronWill
 */
object PhotonServer extends StrictLogging {
	final val MainDir = new File(System.getProperty("user.dir"))
	final val ConfigDir: File = MainDir / "config"
	final val PluginsDir: File = MainDir / "plugins"
	final val WorldsDir: File = MainDir / "worlds"

	def main(args: Array[String]): Unit = {

	}
}