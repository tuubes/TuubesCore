package org.tuubes.core.plugins

/**
 * Trait that must be implemented by the companion object of each plugin's main class. It allows
 * Tuubes to access some plugin's information without creating an instance of that plugin.
 *
 * @see org.tuubes.core.plugins.PluginInfos
 * @see com.electronwill.macros.PluginMain
 * @author TheElectronWill
 */
trait PluginDescription {
  val name: String
  val version: String
  val optionalDeps: Seq[String]
  val requiredDeps: Seq[String]
}
