package org.tuubes.plugin

import better.files.File
import com.typesafe.scalalogging.Logger
import org.tuubes.server.PhotonServer
import org.tuubes.world.World

/**
 * @author TheElectronWill
 */
trait Plugin {
	val name: String
	val version: String
	val requiredDependencies: Iterable[String] = Nil
	val optionalDependencies: Iterable[String] = Nil
	final val directory: File = PhotonServer.DirPlugins / name
	final lazy val logger = Logger(name)

	@volatile private[plugin] final var state = PluginState.LOADED
	/** @return true if the plugin is enabled */
	def isEnabled: Boolean = state == PluginState.ENABLED

	/** @return the worlds where the plugin is enabled */
	def worlds: Iterable[World]
}