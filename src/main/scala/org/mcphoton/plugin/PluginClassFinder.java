package org.mcphoton.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
