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

import com.electronwill.nightconfig.toml.TomlConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mcphoton.Photon;
import org.mcphoton.impl.plugin.DependencyResolver.Solution;
import org.mcphoton.plugin.*;
import org.mcphoton.plugin.GlobalPlugin;
import org.mcphoton.server.Server;
import org.mcphoton.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GlobalPluginsManager
 *
 * @author TheElectronWill
 */
public final class GlobalPluginsManagerImpl implements GlobalPluginsManager {

	private static final Logger log = LoggerFactory.getLogger(GlobalPluginsManagerImpl.class);
	static final ClassSharer GLOBAL_CLASS_SHARER = new ClassSharerImpl();
	static final File PLUGINS_CONFIG = new File(Photon.PLUGINS_DIR, "plugins_config.toml");
	private final Map<String, GlobalPlugin> serverPlugins = new HashMap<>();

	@Override
	public ClassSharer getClassSharer() {
		return GLOBAL_CLASS_SHARER;
	}

	@Override
	public GlobalPlugin getGlobalPlugin(String name) {
		return serverPlugins.get(name);
	}

	@Override
	public boolean isGlobalPluginLoaded(String name) {
		return serverPlugins.containsKey(name);
	}

	/**
	 * Loads all the plugins from the photon's plugins directory.
	 */
	public void loadAllPlugins() throws IOException {
		File[] pluginsFiles = Photon.PLUGINS_DIR.listFiles((file, name) -> name.endsWith(".jar"));
		if (!PLUGINS_CONFIG.exists()) {
			PLUGINS_CONFIG.createNewFile();
		}

		TomlConfig config = new TomlParser().parse(PLUGINS_CONFIG);
		Map<World, List<String>> worldPlugins = new HashMap<>(config.size());
		List<String> serverPlugins = config.getValue("server");
		if(serverPlugins == null) {
			serverPlugins = Collections.emptyList();
		}
		Server server = Photon.getServer();
		Collection<World> serverWorlds = server.getWorlds();
		for (World world : serverWorlds) {
			List<String> plugins = config.getValue(world.getName());
			if (plugins != null) {
				worldPlugins.put(world, plugins);
			}
		}

		loadPlugins(pluginsFiles, worldPlugins, serverPlugins, serverWorlds);
	}

	private void loadOtherPlugin(PluginInfos infos, World world) {
		try {
			loadOtherPlugin(infos.clazz, infos.classLoader, world);
		} catch (Exception ex) {
			log.error("Unable to load the plugin {}.", infos.description.name(), ex);
		}
	}

	private Plugin loadOtherPlugin(Class<? extends Plugin> clazz, PluginClassLoader classLoader, World world) throws Exception {
		Plugin instance = clazz.newInstance();
		instance.onLoad();
		world.getPluginsManager().registerPlugin(instance);
		classLoader.increaseUseCount();
		return instance;
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
			throw new PluginClassNotFoundException(file);
		}
		final PluginDescription description = clazz.getAnnotation(PluginDescription.class);

		if (GlobalPlugin.class.isAssignableFrom(clazz)) {//GlobalPlugin -> one global instance.
			if (description == null) {
				throw new MissingPluginDescriptionException(clazz);
			}
			return loadServerPlugin(clazz, classLoader, description, worlds);
		} else if (WorldPlugin.class.isAssignableFrom(clazz)) {//WorldPlugin -> one instance per world.
			if (description == null) {
				throw new MissingPluginDescriptionException(clazz);
			}
			WorldPlugin instance = null;
			for (World world : worlds) {
				instance = loadWorldPlugin(clazz, classLoader, description, world);
			}
			return instance;//return the plugin instance of the last world.

		} else {//Unknown type of plugin.
			//Don't need to check for a PluginDescription, because this type of plugin doesn't need to be initialized by the PluginsManager.
			Plugin instance = null;
			for (World world : worlds) {
				instance = loadOtherPlugin(clazz, classLoader, world);
			}
			return instance;//return the plugin instance of the last world.
		}
	}

	/**
	 * Loads plugins from multiple files.
	 *
	 * @param files the files to load the plugins from (1 plugin per file).
	 * @param worldPlugins the plugins to load for each world.
	 * @param serverPlugins the plugins to load for the entire server. They don't have to extend GlobalPlugin.
	 * @param serverWorlds the worlds where the serverPlugins will be loaded.
	 */
	@Override
	public void loadPlugins(File[] files, Map<World, List<String>> worldPlugins, List<String> serverPlugins, Collection<World> serverWorlds) {
		final Map<String, PluginInfos> infosMap = new HashMap<>();
		final List<String> serverPluginsVersions = new ArrayList<>(serverPlugins.size());
		final List<PluginInfos> nonGlobalServerPlugins = new ArrayList<>();//for step 4
		final List<Throwable> errors = new ArrayList<>();

		//1: Gather informations about the plugins: class + description.
		log.debug("Gathering informations about the plugins...");
		for (File file : files) {
			try {
				PluginClassLoader classLoader = new PluginClassLoader(file.toURI().toURL(), GLOBAL_CLASS_SHARER);
				Class<? extends Plugin> clazz = PluginClassFinder.findPluginClass(file, classLoader);
				if (clazz == null) {
					throw new PluginClassNotFoundException(file);
				}
				/*
				 * Here we DO need a PluginDescription for every plugin because to resolve the dependencies we
				 * need to have some informations about the plugin before creating its instance.
				 */
				PluginDescription description = clazz.getAnnotation(PluginDescription.class);
				if (description == null) {
					throw new MissingPluginDescriptionException(clazz);
				}
				PluginInfos infos = new PluginInfos(clazz, classLoader, description);
				infosMap.put(description.name(), infos);
				log.trace("Valid plugin found: {} -> infos: {}.", file, infos);
			} catch (Exception | NoClassDefFoundError error) {
				errors.add(error);
			}
		}

		//2: Resolve dependencies for the ServerPlugins and load them.
		//2.1: Resolve dependencies for the *actual* ServerPlugins.
		log.debug("Resolving dependencies for the actual server plugins...");
		DependencyResolver resolver = new DependencyResolver();
		for (Iterator<String> it = serverPlugins.iterator(); it.hasNext();) {
			String plugin = it.next();
			PluginInfos infos = infosMap.get(plugin);
			if (GlobalPlugin.class.isAssignableFrom(infos.clazz)) {//actual GlobalPlugin
				serverPluginsVersions.add(infos.description.version());
				resolver.addToResolve(infos.description);
			} else {//not a GlobalPlugin -> distribute to every world
				it.remove();
				for (Map.Entry<World, List<String>> entry : worldPlugins.entrySet()) {
					entry.getValue().add(plugin);
				}
			}
		}
		Solution solution = resolver.resolve(errors);
		log.debug("Solution: {}", solution.resolvedOrder);

		//2.2: Print informations.
		log.info("{} out of {} server plugins will be loaded.", solution.resolvedOrder.size(), serverPluginsVersions.size());
		for (Throwable ex : solution.errors) {
			log.error(ex.toString());
		}
		errors.clear();

		//2.3: Load the server plugins.
		log.debug("Loading the server plugins...");
		for (String plugin : solution.resolvedOrder) {
			PluginInfos infos = infosMap.get(plugin);
			infos.setWorlds(serverWorlds);//load in every server's world
			loadServerPlugin(infos);
		}

		//3: Resolve dependencies for the other (non server) plugins, per world, and load them.
		log.info("Loading plugins per world...");
		for (Map.Entry<World, List<String>> entry : worldPlugins.entrySet()) {
			final World world = entry.getKey();
			final List<String> plugins = entry.getValue();

			//3.1: Resolve dependencies for the world's plugins.
			log.debug("Resolving dependencies for the plugins of the world {}...", world.getName());
			resolver = new DependencyResolver();
			resolver.addAvailable(serverPlugins, serverPluginsVersions);//the server plugins are available to all the plugins
			for (String plugin : plugins) {
				PluginInfos infos = infosMap.get(plugin);
				resolver.addToResolve(infos.description);
			}
			solution = resolver.resolve(errors);
			log.debug("Solution: {}", solution.resolvedOrder);

			//3.2: Print informations.
			log.info("{} out of {} plugins will be loaded in world {}.", solution.resolvedOrder.size(), plugins.size(), world);
			for (Throwable ex : solution.errors) {
				log.error(ex.toString());
			}

			//3.3: Load the world's plugins.
			log.debug("Loading the plugins in world {}...", world.getName());
			for (String plugin : solution.resolvedOrder) {
				PluginInfos infos = infosMap.get(plugin);
				try {
					if (GlobalPlugin.class.isAssignableFrom(infos.clazz)) {
						//ServerPlugins need a Collection<World> so we "collect" all the worlds first and load them later, at step 4.
						infos.getWorlds().add(world);
						nonGlobalServerPlugins.add(infos);
					} else if (WorldPlugin.class.isAssignableFrom(infos.clazz)) {
						loadWorldPlugin(infos, world);
					} else {
						loadOtherPlugin(infos, world);
					}
				} catch (Exception ex) {
					log.error("Unable to load the plugin {}.", plugin, ex);
				}
			}
		}

		//4: Actually load the server plugins that aren't loaded on the entire server.
		log.info("Loading the non-global server plugins...");
		for (PluginInfos infos : nonGlobalServerPlugins) {
			loadServerPlugin(infos);
		}
	}

	private void loadServerPlugin(PluginInfos infos) {
		try {
			loadServerPlugin(infos.clazz, infos.classLoader, infos.description, infos.worlds);
		} catch (Exception ex) {
			log.error("Unable to load the server plugin {}.", infos.description.name(), ex);
		}
	}

	private GlobalPlugin loadServerPlugin(Class clazz, PluginClassLoader classLoader, PluginDescription description, Collection<World> worlds) throws Exception {
		GlobalPlugin instance = (GlobalPlugin) clazz.newInstance();
		instance.init(description, worlds);
		instance.onLoad();
		for (World world : worlds) {
			world.getPluginsManager().registerPlugin(instance);
			classLoader.increaseUseCount();
		}
		this.serverPlugins.put(instance.getName(), instance);
		return instance;
	}

	private void loadWorldPlugin(PluginInfos infos, World world) {
		try {
			loadWorldPlugin(infos.clazz, infos.classLoader, infos.description, world);
		} catch (Exception ex) {
			log.error("Unable to load the world plugin {}.", infos.description.name(), ex);
		}
	}

	private WorldPlugin loadWorldPlugin(Class clazz, PluginClassLoader classLoader, PluginDescription description, World world) throws Exception {
		WorldPlugin instance = (WorldPlugin) clazz.newInstance();
		instance.init(description, world);
		instance.onLoad();
		world.getPluginsManager().registerPlugin(instance);
		classLoader.increaseUseCount();
		return instance;
	}

	@Override
	public void unloadAllPlugins() {
		for (GlobalPlugin globalPlugin : serverPlugins.values()) {
			try {
				unloadGlobalPlugin(globalPlugin);
			} catch (Exception ex) {
				log.error("Unable to unload the server plugin {}.", globalPlugin.getName(), ex);
			}
		}
		for (World world : Photon.getServer().getWorlds()) {
			world.getPluginsManager().unloadAllPlugins();
		}
	}

	@Override
	public void unloadGlobalPlugin(GlobalPlugin plugin) throws Exception {
		plugin.onUnload();
		SharedClassLoader classLoader = (SharedClassLoader) plugin.getClass().getClassLoader();
		for (World world : plugin.getActiveWorlds()) {
			world.getPluginsManager().unregisterPlugin(plugin);
			classLoader.decreaseUseCount();
		}
		GLOBAL_CLASS_SHARER.removeUselessClassLoader(classLoader);
		serverPlugins.remove(plugin.getName(), plugin);
	}

	@Override
	public void unloadGlobalPlugin(String name) throws Exception {
		unloadGlobalPlugin(serverPlugins.get(name));
	}
}