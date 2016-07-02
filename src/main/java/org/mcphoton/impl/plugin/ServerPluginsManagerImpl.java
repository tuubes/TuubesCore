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

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mcphoton.Photon;
import org.mcphoton.plugin.ClassSharer;
import org.mcphoton.plugin.Plugin;
import org.mcphoton.plugin.PluginLoader;
import org.mcphoton.plugin.PluginLoadingException;
import org.mcphoton.plugin.ServerPlugin;
import org.mcphoton.plugin.ServerPluginsManager;
import org.mcphoton.plugin.SharedClassLoader;
import org.mcphoton.plugin.WorldPlugin;
import org.mcphoton.plugin.WorldPluginsManager;
import org.mcphoton.world.World;

/**
 * Implementation of ServerPluginsManager
 *
 * @author TheElectronWill
 */
public final class ServerPluginsManagerImpl implements ServerPluginsManager {

	private final Map<String, ServerPlugin> loadedServerPlugins = new ConcurrentHashMap<>();
	private final ClassSharer classSharer = new ClassSharerImpl();
	private volatile PluginLoader defaultPluginLoader;

	public ServerPluginsManagerImpl() {
		this.defaultPluginLoader = new JavaPluginLoader(classSharer);
	}

	@Override
	public ServerPlugin getServerPlugin(String name) {
		return loadedServerPlugins.get(name);
	}

	@Override
	public <T extends Plugin> T loadPlugin(File file, PluginLoader<T> loader) throws PluginLoadingException {
		return loadPlugin(file, loader, Photon.getServer().getWorlds());
	}

	@Override
	public <T extends Plugin> T loadPlugin(File file, PluginLoader<T> loader, Collection<World> worlds) throws PluginLoadingException {
		T plugin = loader.loadPlugin(file);
		if (plugin instanceof ServerPlugin) {
			loadServerPlugin((ServerPlugin) plugin, loader, worlds);
		} else if (plugin instanceof WorldPlugin) {
			loadWorldPlugin((WorldPlugin) plugin, loader, worlds);
		} else {
			loadOtherPlugin(plugin, loader, worlds);
		}
		return plugin;
	}

	private void loadServerPlugin(ServerPlugin plugin, PluginLoader loader, Collection<World> worlds) {
		plugin.init(loader, worlds);
		SharedClassLoader classLoader = (SharedClassLoader) plugin.getClass().getClassLoader();
		for (World world : worlds) {
			WorldPluginsManager pluginsManager = world.getPluginsManager();
			pluginsManager.registerPlugin(plugin);
			pluginsManager.getClassSharer().addClassLoader(classLoader);
		}
		loadedServerPlugins.put(plugin.getName(), plugin);
	}

	private void loadWorldPlugin(WorldPlugin plugin, PluginLoader loader, Collection<World> worlds) {
		SharedClassLoader classLoader = (SharedClassLoader) plugin.getClass().getClassLoader();
		for (World world : worlds) {
			plugin.init(loader, world);
			WorldPluginsManager pluginsManager = world.getPluginsManager();
			pluginsManager.registerPlugin(plugin);
			pluginsManager.getClassSharer().addClassLoader(classLoader);
		}
	}

	private void loadOtherPlugin(Plugin plugin, PluginLoader loader, Collection<World> worlds) {
		ClassLoader classLoader = plugin.getClass().getClassLoader();
		for (World world : worlds) {
			WorldPluginsManager pluginsManager = world.getPluginsManager();
			pluginsManager.registerPlugin(plugin);
			if (classLoader instanceof SharedClassLoader) {
				pluginsManager.getClassSharer().addClassLoader((SharedClassLoader) classLoader);
			}
		}
	}

	@Override
	public void unloadServerPlugin(ServerPlugin plugin) {
		try {
			plugin.onUnload();
		} finally {
			SharedClassLoader classLoader = (SharedClassLoader) plugin.getClass().getClassLoader();
			for (World world : plugin.getActiveWorlds()) {
				WorldPluginsManager pluginsManager = world.getPluginsManager();
				pluginsManager.unregisterPlugin(plugin);
				pluginsManager.getClassSharer().removeClassLoader(classLoader);
			}
			plugin.getLoader().unloadPlugin(plugin);
			loadedServerPlugins.remove(plugin.getName());
		}
	}

	@Override
	public void unloadServerPlugin(String name) {
		unloadServerPlugin(getServerPlugin(name));
	}

	@Override
	public boolean isServerPluginLoaded(String name) {
		return loadedServerPlugins.containsKey(name);
	}

	@Override
	public PluginLoader getDefaultPluginLoader() {
		return defaultPluginLoader;
	}

	@Override
	public void setDefaultPluginLoader(PluginLoader<? extends Plugin> loader) {
		defaultPluginLoader = loader;
	}

	@Override
	public ClassSharer getClassSharer() {
		return classSharer;
	}

}
