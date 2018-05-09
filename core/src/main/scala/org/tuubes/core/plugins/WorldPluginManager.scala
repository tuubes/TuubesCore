package org.tuubes.core.plugins

trait WorldPluginManager {
	def enable(plugin: Plugin)

	def disable(plugin: Plugin)
}