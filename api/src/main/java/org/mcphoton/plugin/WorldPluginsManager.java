package org.mcphoton.plugin;

import java.io.File;
import java.util.List;

/**
 * Manages plugins for one world.
 *
 * @author TheElectronWill
 */
public interface WorldPluginsManager {
	/**
	 * Gets a plugin by its name. If the plugin is not loaded by this manager, this method returns
	 * <code>null</code>. The returned plugin may be of any type (WorldPlugin, GlobalPlugin, or any
	 * other Plugin implementation).
	 *
	 * @return the plugin with that name, or <code>null</code>.
	 */
	Plugin getPlugin(String name);

	/**
	 * Loads a plugin from a file.
	 *
	 * @return the loaded plugin.
	 */
	Plugin loadPlugin(File file) throws Exception;

	/**
	 * Loads multiple plugins from multiple files (1 plugin per file).
	 *
	 * @return a list of the loaded plugins.
	 */
	List<Plugin> loadPlugins(File[] files);

	/**
	 * Unloads every plugin loaded by this WorldPluginsManager.
	 */
	void unloadAllPlugins();

	/**
	 * Unloads a plugin. If the plugin is a GlobalPlugin it isn't disabled entirely but only in this
	 * world, ie everything it registered in the managers of this world is unregistered.
	 */
	void unloadPlugin(Plugin plugin) throws Exception;

	/**
	 * Unloads a plugin. If the plugin is a GlobalPlugin it isn't disabled entirely but only in this
	 * world, ie everything it registered in the managers of this world is unregistered.
	 */
	void unloadPlugin(String name) throws Exception;

	/**
	 * Checks if a plugin with that name is loaded by this WorldPluginsManager.
	 */
	boolean isPluginLoaded(String name);

	/**
	 * Registers a plugin to this PluginsManager. After this method, the plugin will be considered
	 * as "loaded" by this PluginsManager.
	 */
	void registerPlugin(Plugin plugin);

	/**
	 * Unregisters a plugin from this PluginsManager.After this method, the plugin will no longer be
	 * considered as "loaded" by this PluginsManager.
	 */
	void unregisterPlugin(Plugin plugin);
}