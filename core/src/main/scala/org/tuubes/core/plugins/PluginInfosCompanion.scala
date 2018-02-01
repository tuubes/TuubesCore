package org.tuubes.core.plugins

/**
 * Trait that must be implemented by the companion object of each plugin's main class. It allows
 * Photon to access some plugin's information without creating an instance of that plugin.
 *
 * @see org.tuubes.core.plugins.PluginInfos
 * @see com.electronwill.macros.PluginMain
 * @author TheElectronWill
 */
trait PluginInfosCompanion {
	def Name: String
	def Version: String
	def OptionalDeps: Seq[String]
	def RequiredDeps: Seq[String]
}