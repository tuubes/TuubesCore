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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.mcphoton.plugin.ClassSharer;
import org.mcphoton.plugin.Plugin;
import org.mcphoton.plugin.PluginLoader;
import org.mcphoton.plugin.PluginLoadingException;
import org.mcphoton.plugin.ServerPlugin;
import org.mcphoton.plugin.WorldPlugin;

/**
 * Loads Java plugins. The plugins may be of any type, not necessarily {@link WorldPlugin} or
 * {@link ServerPlugin}. The only condition for the JavaPluginLoader to be able to load a plugin is that it
 * must have a default constructor (without parameters).
 *
 * @author TheElectronWill
 */
public final class JavaPluginLoader implements PluginLoader<Plugin> {

	private final ClassSharer sharer;

	public JavaPluginLoader() {
		this.sharer = new ClassSharerImpl();
	}

	public JavaPluginLoader(ClassSharer sharer) {
		this.sharer = sharer;
	}

	@Override
	public Plugin loadPlugin(File file) throws PluginLoadingException {
		try {
			PluginClassLoader loader = new PluginClassLoader(file.toURI().toURL(), sharer);
			sharer.addClassLoader(loader);
			return createPluginInstance(file, loader);
		} catch (Exception e) {
			throw new PluginLoadingException(e);
		}
	}

	/**
	 * {@inheritDoc}. This method does not call any plugin's method. In particular, it does not call the
	 * {@link WorldPlugin#init(PluginLoader, org.mcphoton.world.World)} method, nor the
	 * {@link ServerPlugin#init(PluginLoader, java.util.Collection)} method.
	 */
	@Override
	public List<Plugin> loadPlugins(File[] files) {
		// step 1 - creating ClassLoaders
		// We add all the ClassLoaders first so that all the needed classes are available when the plugins' instances are created.
		PluginClassLoader[] loaders = new PluginClassLoader[files.length];
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			try {
				PluginClassLoader loader = new PluginClassLoader(f.toURI().toURL(), sharer);
				loaders[i] = loader;
				sharer.addClassLoader(loader);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// step 2 - creating instances of plugins
		List<Plugin> plugins = new ArrayList<>(files.length);
		for (int i = 0; i < files.length; i++) {
			PluginClassLoader loader = loaders[i];
			if (loader != null) {
				try {
					Plugin plugin = createPluginInstance(files[i], loader);
					plugins.add(plugin);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return plugins;
		// the PluginsManager is responsible for calling init(), onLoad(), onUnload(), and for managing dependencies.
	}

	/**
	 * Creates a plugin instance by looking for the class that implements Plugin and using the default
	 * constructor.
	 */
	private Plugin createPluginInstance(File file, PluginClassLoader loader)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		try (JarFile jar = new JarFile(file)) {
			Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (!entryName.endsWith(".class")) {
					continue;
				}

				String className = entryName.replace('/', '.').replaceAll(".class", "");
				Class<?> clazz = loader.loadClass(className);
				if (Plugin.class.isAssignableFrom(clazz)) {
					return (Plugin) clazz.newInstance();
				}
			}
			return null;
		}
	}

	@Override
	public void unloadPlugin(Plugin plugin) {
		PluginClassLoader loader = (PluginClassLoader) plugin.getClass().getClassLoader();
		sharer.removeClassLoader(loader);
	}

}
