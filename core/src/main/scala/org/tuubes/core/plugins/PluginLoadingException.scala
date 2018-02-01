package org.tuubes.core.plugins

/**
 * @author TheElectronWill
 */
class PluginLoadingException(msg: String, cause: Throwable) extends Exception(msg, cause) {
	def this(msg: String) = this(msg, null)
}