package org.mcphoton.plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.mcphoton.world.World;

/**
 * Manages GlobalPlugins. It can load WorldPlugins (see the documentation of the loadPlugin
 * methods), but can only unload and get ServerPlugins.
 *
 * @author TheElectronWill
 */
public interface GlobalPluginsManager {
	/**
	 * Gets a GlobalPlugin by its name.
	 *
	 * @return the GlobalPlugin with that name, or <code>null</code>.
	 */
	GlobalPlugin getGlobalPlugin(String name);

	/**
	 * Loads a plugin from a file in every server's world.
	 * <p>
	 * If the plugin is a WorldPlugin, then one instance of the plugin is created per world, and the
	 * instance assigned to the default world is returned. If the plugin is a GlobalPlugin, only
	 * one instance of the plugin is created and returned.
	 * </p>
	 *
	 * @return the loaded plugin.
	 */
	Plugin loadPlugin(File file) throws Exception;

	/**
	 * Loads a plugin from a file in the specified worlds.
	 * <p>
	 * If the plugin is a WorldPlugin, then one instance of the plugin is created per world, and the
	 * instance assigned to the first specified world (the first parameter) is returned. If the
	 * plugin is a GlobalPlugin, only one instance of the plugin is created and returned.
	 * </p>
	 *
	 * @return the loaded plugin.
	 */
	Plugin loadPlugin(File file, Collection<World> worlds) throws Exception;

	/**
	 * Loads multiple plugins from multiple files.
	 *
	 * @param files         the files to load the plugins from (1 plugin per file).
	 * @param worldPlugins  the plugins to load for each world.
	 * @param serverPlugins the plugins to load for the entire server. They don't have to extend
	 *                      GlobalPlugin.
	 * @param serverWorlds  the worlds where the serverPlugins will be loaded.
	 */
	void loadPlugins(File[] files, Map<World, List<String>> worldPlugins,
					 List<String> serverPlugins, Collection<World> serverWorlds);

	/**
	 * Unloads all the plugins: all the ServerPlugins + all the plugins of every world.
	 */
	void unloadAllPlugins();

	/**
	 * Unloads a GlobalPlugin completely (from all worlds).
	 */
	void unloadGlobalPlugin(GlobalPlugin plugin) throws Exception;

	/**
	 * Unloads a GlobalPlugin completely (from all worlds).
	 */
	void unloadGlobalPlugin(String name) throws Exception;

	/**
	 * Checks if a GlobalPlugin with the specified name is loaded by this GlobalPluginsManager.
	 *
	 * @return <code>true</code> if a GlobalPlugin with that name is loaded, <true>false</false>
	 * otherwise.
	 */
	boolean isGlobalPluginLoaded(String name);

	/**
	 * Gets the class sharer used by the ServerPluginsManagers to share classes across the
	 * ServerPlugins.
	 */
	ClassSharer getClassSharer();
}