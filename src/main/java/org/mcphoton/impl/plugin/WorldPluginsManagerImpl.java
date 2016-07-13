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
import java.util.List;
import java.util.Map;
import org.mcphoton.impl.plugin.DependencyResolver.Solution;
import static org.mcphoton.impl.plugin.ServerPluginsManagerImpl.GLOBAL_CLASS_SHARER;
import static org.mcphoton.impl.plugin.ServerPluginsManagerImpl.LOGGER;
import org.mcphoton.plugin.Plugin;
import org.mcphoton.plugin.PluginDescription;
import org.mcphoton.plugin.ServerPlugin;
import org.mcphoton.plugin.SharedClassLoader;
import org.mcphoton.plugin.WorldPlugin;
import org.mcphoton.plugin.WorldPluginsManager;
import org.mcphoton.world.World;

/**
 * Implementation of WorldPluginsManager.
 *
 * @author TheElectronWill
 */
public final class WorldPluginsManagerImpl implements WorldPluginsManager {

	private final Map<String, Plugin> plugins = new HashMap<>();
	private final World world;

	public WorldPluginsManagerImpl(World world) {
		this.world = world;
	}

	@Override
	public Plugin getPlugin(String name) {
		return plugins.get(name);
	}

	@Override
	public boolean isPluginLoaded(String name) {
		return plugins.containsKey(name);
	}

	@Override
	public Plugin loadPlugin(File file) throws Exception {
		final PluginClassLoader classLoader = new PluginClassLoader(file.toURI().toURL(), GLOBAL_CLASS_SHARER);
		final Class<? extends Plugin> clazz = PluginClassFinder.findPluginClass(file, classLoader);
		if (clazz == null) {
			throw new PluginClassNotFoundException(file);
		}
		final PluginDescription description = clazz.getAnnotation(PluginDescription.class);
		final Plugin instance = clazz.newInstance();

		if (ServerPlugin.class.isAssignableFrom(clazz)) {
			if (description == null) {
				throw new MissingPluginDescriptionException(clazz);
			}
			Collection<World> worlds = Collections.synchronizedCollection(new SimpleBag<>(world));
			((ServerPlugin) instance).init(description, worlds);
		} else if (WorldPlugin.class.isAssignableFrom(clazz)) {
			if (description == null) {
				throw new MissingPluginDescriptionException(clazz);
			}
			((WorldPlugin) instance).init(description, world);
		}

		instance.onLoad();
		classLoader.increaseUseCount();
		plugins.put(instance.getName(), instance);
		return instance;
	}

	@Override
	public List<Plugin> loadPlugins(File[] files) {
		final Map<String, PluginInfos> infosMap = new HashMap<>();
		final List<Plugin> loadedPlugins = new ArrayList<>(files.length);
		final List<Exception> errors = new ArrayList<>();

		//1: Gather informations about the plugins: class + description.
		LOGGER.debug("Gathering informations about the plugins...");
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
				LOGGER.trace("Valid plugin found: {} -> infos: {}.", file, infos);
			} catch (Exception ex) {
				errors.add(ex);
			}
		}

		//2: Resolve the dependencies.
		LOGGER.debug("Resolving plugins' dependencies...");
		DependencyResolver resolver = new DependencyResolver();
		for (PluginInfos infos : infosMap.values()) {
			resolver.addToResolve(infos.description);
		}
		Solution solution = resolver.resolve(errors);
		LOGGER.debug("Solution: {}", solution.resolvedOrder);

		//3: Print informations.
		LOGGER.info("{} out of {} server plugins will be loaded.", solution.resolvedOrder.size(), files.length);
		for (Exception ex : solution.errors) {
			LOGGER.error(ex.toString());
		}

		//4: Load the plugins.
		LOGGER.debug("Loading the plugins...");
		for (String plugin : solution.resolvedOrder) {
			try {
				PluginInfos infos = infosMap.get(plugin);
				Plugin instance = infos.clazz.newInstance();

				if (ServerPlugin.class.isAssignableFrom(infos.clazz)) {
					Collection<World> worlds = Collections.synchronizedCollection(new SimpleBag<>(world));
					((ServerPlugin) instance).init(infos.description, worlds);
				} else if (WorldPlugin.class.isAssignableFrom(infos.clazz)) {
					((WorldPlugin) instance).init(infos.description, world);
				}

				instance.onLoad();
				infos.classLoader.increaseUseCount();
				plugins.put(instance.getName(), instance);
				loadedPlugins.add(instance);
			} catch (Exception ex) {
				LOGGER.error("Unable to load the plugin {} in world {}.", plugin, world.getName());
			}
		}
		return loadedPlugins;
	}

	@Override
	public void registerPlugin(Plugin plugin) {
		plugins.put(plugin.getName(), plugin);
	}

	@Override
	public void unloadAllPlugins() {
		for (Plugin plugin : plugins.values()) {
			try {
				unloadPlugin(plugin);
			} catch (Exception ex) {
				LOGGER.error("Unable to unload the plugin {}", plugin, ex);
			}
		}
	}

	@Override
	public void unloadPlugin(Plugin plugin) throws Exception {
		plugin.onUnload();
		SharedClassLoader classLoader = (SharedClassLoader) plugin.getClass().getClassLoader();
		classLoader.decreaseUseCount();
		GLOBAL_CLASS_SHARER.removeUselessClassLoader(classLoader);
		plugins.remove(plugin.getName(), plugin);
	}

	@Override
	public void unloadPlugin(String name) throws Exception {
		unloadPlugin(plugins.get(name));
	}

	@Override
	public void unregisterPlugin(Plugin plugin) {
		plugins.remove(plugin.getName(), plugin);
	}

}
