package org.mcphoton.plugin

/**
 * Trait that must be implemented by the companion object of each plugin's main class. It allows
 * Photon to access some plugin's information without creating an instance of that plugin.
 *
 * @author TheElectronWill
 */
trait PluginInfosCompanion {
	def Name: String
	def Version: String
	def OptionalDependencies: Seq[String]
	def RequiredDependencies: Seq[String]
}