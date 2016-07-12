/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 *
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.impl.plugin;

import com.electronwill.utils.SimpleBag;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mcphoton.Photon;
import org.mcphoton.impl.plugin.DependencyResolver.Solution;
import org.mcphoton.plugin.ClassSharer;
import org.mcphoton.plugin.Plugin;
import org.mcphoton.plugin.PluginDescription;
import org.mcphoton.plugin.ServerPlugin;
import org.mcphoton.plugin.ServerPluginsManager;
import org.mcphoton.plugin.SharedClassLoader;
import org.mcphoton.plugin.WorldPlugin;
import org.mcphoton.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of ServerPluginsManager
 *
 * @author TheElectronWill
 */
public final class ServerPluginsManagerImpl implements ServerPluginsManager {

	static final Logger LOGGER = LoggerFactory.getLogger("PluginsManager");
	static final ClassSharer GLOBAL_CLASS_SHARER = new ClassSharerImpl();
	private final Map<String, ServerPlugin> serverPlugins = new HashMap<>();

	@Override
	public ClassSharer getClassSharer() {
		return GLOBAL_CLASS_SHARER;
	}

	@Override
	public ServerPlugin getServerPlugin(String name) {
		return serverPlugins.get(name);
	}

	@Override
	public boolean isServerPluginLoaded(String name) {
		return serverPlugins.containsKey(name);
	}

	@Override
	public Plugin loadPlugin(File file) throws Exception {
		return loadPlugin(file, Photon.getServer().getWorlds());
	}

	@Override
	public Plugin loadPlugin(File file, Collection<World> worlds) throws Exception {
		final PluginClassLoader classLoader = new PluginClassLoader(file.toURI().toURL(), GLOBAL_CLASS_SHARER);
		final Class<? extends Plugin> clazz = PluginClassFinder.findPluginClass(file, classLoader);
		if (clazz == null) {
			throw new PluginClassNotFoundException("No suitable plugin class found in " + file);
		}
		final PluginDescription description = clazz.getAnnotation(PluginDescription.class);

		if (ServerPlugin.class.isAssignableFrom(clazz)) {//ServerPlugin -> one global instance.
			if (description == null) {
				throw new MissingPluginDescriptionException("Missing annotation @PluginDescription for class " + clazz);
			}
			ServerPlugin instance = (ServerPlugin) clazz.newInstance();

			Collection<World> worldsCopy = new SimpleBag<>(worlds.size());
			worldsCopy.addAll(worlds);

			instance.init(description, worldsCopy);
			for (World world : worldsCopy) {
				world.getPluginsManager().registerPlugin(instance);
				classLoader.increaseUseCount();
			}
			return instance;

		} else if (WorldPlugin.class.isAssignableFrom(clazz)) {//WorldPlugin -> one instance per world.
			if (description == null) {
				throw new MissingPluginDescriptionException("Missing annotation @PluginDescription for class " + clazz);
			}
			WorldPlugin instance = null;
			for (World world : worlds) {
				instance = (WorldPlugin) clazz.newInstance();
				instance.init(description, world);
				world.getPluginsManager().registerPlugin(instance);
				classLoader.increaseUseCount();
			}
			return instance;

		} else {//Unknown type of plugin.
			//Don't check for a PluginDescription annotation, because this type of plugin doesn't need to be initialized by the PluginsManager.
			Plugin instance = null;
			for (World world : worlds) {
				instance = clazz.newInstance();
				world.getPluginsManager().registerPlugin(instance);
				classLoader.increaseUseCount();
			}
			return instance;
		}
	}

	/**
	 * Loads plugins from multiple files.
	 *
	 * @param files the files to load the plugins from (1 plugin per file).
	 * @param worldPlugins the plugins to load for each world.
	 * @param serverPlugins the plugins to load for the entire server. They don't have to extend ServerPlugin.
	 */
	public void loadPlugins(File[] files, Map<World, List<String>> worldPlugins, List<String> serverPlugins, List<World> serverWorlds) throws Exception {
		final Map<String, Class<? extends Plugin>> pluginsClasses = new HashMap<>();
		final Map<String, Collection<World>> pluginsWorlds = new HashMap<>();
		final List<String> serverPluginsVersions = new ArrayList<>(serverPlugins.size());

		//1: Find the class of each plugin.
		for (File file : files) {
			PluginClassLoader classLoader = new PluginClassLoader(file.toURI().toURL(), GLOBAL_CLASS_SHARER);
			Class<? extends Plugin> clazz = PluginClassFinder.findPluginClass(file, classLoader);
			if (clazz == null) {
				throw new PluginClassNotFoundException("No suitable plugin class found in " + file);
			}
			/*
			 * Here we DO need a PluginDescription for every plugin because to resolve the dependencies we
			 * need to have some informations about the plugin before creating its instance.
			 */
			PluginDescription description = clazz.getAnnotation(PluginDescription.class);
			if (description == null) {
				throw new MissingPluginDescriptionException("Missing annotation @PluginDescription for class " + clazz);
			}
			pluginsClasses.put(description.name(), clazz);
		}

		//2: Resolve dependencies for the *actual* ServerPlugins.
		DependencyResolver resolver = new DependencyResolver();
		for (Iterator<String> it = serverPlugins.iterator(); it.hasNext();) {
			String plugin = it.next();
			Class<? extends Plugin> clazz = pluginsClasses.get(plugin);
			if (ServerPlugin.class.isAssignableFrom(clazz)) {//actual ServerPlugin
				PluginDescription description = clazz.getAnnotation(PluginDescription.class);
				serverPluginsVersions.add(description.version());
				resolver.addToResolve(description);
			} else {//not a ServerPlugin -> distribute to every world
				it.remove();
				for (Map.Entry<World, List<String>> entry : worldPlugins.entrySet()) {
					entry.getValue().add(plugin);
				}
			}
		}
		Solution solution = resolver.resolve();

		//3: Load the server plugins.
		LOGGER.info("{} out of {} server plugins will be loaded.", solution.resolvedOrder.size(), serverPluginsVersions.size());
		for (Exception ex : solution.errors) {
			LOGGER.error(ex.toString());
		}
		for (String plugin : solution.resolvedOrder) {
			Class<? extends Plugin> clazz = pluginsClasses.get(plugin);
			try {
				ServerPlugin instance = (ServerPlugin) clazz.newInstance();
				PluginDescription description = clazz.getAnnotation(PluginDescription.class);
				instance.init(description, serverWorlds);
				instance.onLoad();
			} catch (Exception ex) {
				LOGGER.error("Unable to load the plugin {}.", plugin, ex);
			}
		}

		//4: Resolve dependencies for the other plugins, per world, and load them.
		for (Map.Entry<World, List<String>> entry : worldPlugins.entrySet()) {
			final World world = entry.getKey();
			final List<String> plugins = entry.getValue();

			//4.1: Resolve dependencies for the world's plugins.
			resolver = new DependencyResolver();
			resolver.addAvailable(serverPlugins, serverPluginsVersions);//the server plugins are available to all the plugins

			for (String plugin : plugins) {
				Class<? extends Plugin> clazz = pluginsClasses.get(plugin);
				PluginDescription description = clazz.getAnnotation(PluginDescription.class);
				resolver.addToResolve(description);
			}
			solution = resolver.resolve();

			//4.2: Load the world's plugins.
			entry.setValue(solution.resolvedOrder);
			LOGGER.info("{} out of {} plugins will be loaded in world {}.", solution.resolvedOrder.size(), plugins.size(), world);
			for (Exception ex : solution.errors) {
				LOGGER.error(ex.toString());
			}

			for (String plugin : solution.resolvedOrder) {
				final Class<? extends Plugin> clazz = pluginsClasses.get(plugin);
				try {
					if (ServerPlugin.class.isAssignableFrom(clazz)) {
						//ServerPlugin needs a Collection<World> so we "collect" all the worlds first and we'll load the plugin later
						Collection<World> pluginWorlds = pluginsWorlds.get(plugin);
						if (pluginWorlds == null) {//need to create a Collection<World>
							pluginWorlds = Collections.synchronizedCollection(new SimpleBag<>());//synchronized because any thread could use it
							pluginsWorlds.put(plugin, pluginWorlds);
						}
						pluginWorlds.add(world);
					} else if (WorldPlugin.class.isAssignableFrom(clazz)) {
						PluginDescription description = clazz.getAnnotation(PluginDescription.class);
						WorldPlugin instance = (WorldPlugin) clazz.newInstance();
						instance.init(description, world);
						instance.onLoad();
						world.getPluginsManager().registerPlugin(instance);
					} else {
						Plugin instance = clazz.newInstance();
						instance.onLoad();
						world.getPluginsManager().registerPlugin(instance);
					}
					SharedClassLoader loader = (SharedClassLoader) clazz.getClassLoader();
					loader.increaseUseCount();
				} catch (Exception ex) {
					LOGGER.error("Unable to load the plugin {}.", plugin, ex);
				}
			}
		}

		//5: Actually load the server plugins that aren't loaded on the entire server.
		for (Map.Entry<String, Collection<World>> entry : pluginsWorlds.entrySet()) {
			final String plugin = entry.getKey();
			final Collection<World> worlds = entry.getValue();
			final Class<? extends Plugin> clazz = pluginsClasses.get(plugin);
			try {
				PluginDescription description = clazz.getAnnotation(PluginDescription.class);

				ServerPlugin instance = (ServerPlugin) clazz.newInstance();
				instance.init(description, worlds);
				instance.onLoad();

				for (World world : worlds) {
					world.getPluginsManager().registerPlugin(instance);
				}
			} catch (Exception ex) {
				LOGGER.error("Unable to load the plugin {}.", plugin, ex);
			}
		}
	}

	@Override
	public void unloadServerPlugin(ServerPlugin plugin) {
		; //TODO
	}

	@Override
	public void unloadServerPlugin(String name) {
		; //TODO
	}

}
