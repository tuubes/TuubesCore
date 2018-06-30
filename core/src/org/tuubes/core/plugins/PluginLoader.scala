package org.tuubes.core.plugins

import better.files.{File, Files}

/**
 * Loads and unloads Tuubes plugins.
 */
trait PluginLoader {

  /**
	 * Loads one or more plugins from files.
	 *
	 * @param files the plugins' files, one file per plugin
	 * @return the number of loaded plugins
	 */
  def load(files: Files): Int

  /**
	 * Loads one or more plugins from files.
	 *
	 * @param files the plugins' files, one file per plugin
	 * @return the number of loaded plugins
	 */
  def load(files: File*): Int = load(files.iterator)

  /**
	 * Unloads a plugin and its hard dependents.
	 *
	 * @param p the plugin to unload
	 */
  def unload(p: Plugin): Unit

  /**
	 * Unloads all the plugins.
	 */
  def unloadAll(): Unit

  /**
	 * @return the currently loaded plugins
	 */
  def plugins: Iterable[Plugin]

  /**
	 * Gets a plugin by its name.
	 *
	 * @param name the plugin's unique name
	 * @return the loaded plugin with the corresponding name, or None
	 */
  def plugin(name: String): Option[Plugin]
}
