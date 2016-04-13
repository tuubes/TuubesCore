package org.mcphoton.impl.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.mcphoton.plugin.JavaPlugin;
import org.mcphoton.plugin.PluginLoader;
import org.mcphoton.plugin.PluginLoadingException;

public class JavaPluginLoader implements PluginLoader<JavaPlugin> {

	private final PhotonClassSharer sharer = new PhotonClassSharer();

	@Override
	public JavaPlugin loadPlugin(File file) throws PluginLoadingException {
		try {
			PluginClassLoader loader = new PluginClassLoader(file.toURI().toURL(), sharer);
			sharer.addClassLoader(loader);
			return createPluginInstance(file, loader);
		} catch (Exception e) {
			throw new PluginLoadingException(e);
		}
	}

	@Override
	public List<JavaPlugin> loadPlugins(File[] files) {
		// step 1 - creating ClassLoaders
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
		List<JavaPlugin> plugins = new ArrayList<>(files.length);
		for (int i = 0; i < files.length; i++) {
			PluginClassLoader loader = loaders[i];
			if (loader != null) {
				try {
					JavaPlugin plugin = createPluginInstance(files[i], loader);
					plugin.init(this);
					plugins.add(plugin);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return plugins;
		// the PluginsManager is responsible for calling onLoad() and onUnload(), and for managing dependencies.
	}

	private JavaPlugin createPluginInstance(File file, PluginClassLoader loader)
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
				if (JavaPlugin.class.isAssignableFrom(clazz)) {
					return (JavaPlugin) clazz.newInstance();
				}
			}
			return null;
		}
	}

	@Override
	public void unloadPlugin(JavaPlugin plugin) {
		PluginClassLoader loader = (PluginClassLoader) plugin.getClass().getClassLoader();
		sharer.removeClassLoader(loader);
	}

}
