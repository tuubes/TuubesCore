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
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.mcphoton.plugin.Plugin;

/**
 *
 * @author TheElectronWill
 */
public class PluginClassFinder {

	public static Class<? extends Plugin> findPluginClass(File file, PluginClassLoader loader) throws ClassNotFoundException, IOException {
		try (JarFile jar = new JarFile(file)) {
			Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (!entryName.endsWith(".class")) {
					continue;
				}

				String className = entryName.replace('/', '.').replaceAll(".class", "");
				Class clazz = loader.findClass(className, false);
				if (Plugin.class.isAssignableFrom(clazz)) {
					return clazz;
				}
			}
			return null;
		}
	}

	private PluginClassFinder() {
	}

}
